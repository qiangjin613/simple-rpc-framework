package cn.qiangjin.dev.tech.rpc.transport.netty.codec;

import cn.qiangjin.dev.tech.rpc.transport.command.Command;
import cn.qiangjin.dev.tech.rpc.transport.command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码 byte -> msg
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public abstract class CommandDecoder extends ByteToMessageDecoder {

    private static final int LENGTH_FIELD_LENGTH = Integer.BYTES;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
        if (!in.isReadable(LENGTH_FIELD_LENGTH)) {
            return;
        }
        in.markReaderIndex();
        int length = in.readInt() - LENGTH_FIELD_LENGTH;

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        Header header = decodeHeader(channelHandlerContext, in);
        int payloadLength  = length - header.length();
        byte [] payload = new byte[payloadLength];
        in.readBytes(payload);
        out.add(new Command(header, payload));
    }

    protected abstract Header decodeHeader(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) ;
}
