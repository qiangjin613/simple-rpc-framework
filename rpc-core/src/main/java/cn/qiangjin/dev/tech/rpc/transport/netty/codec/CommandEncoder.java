package cn.qiangjin.dev.tech.rpc.transport.netty.codec;

import cn.qiangjin.dev.tech.rpc.transport.command.Command;
import cn.qiangjin.dev.tech.rpc.transport.command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码 msg -> byte
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public abstract class CommandEncoder extends MessageToByteEncoder<Command> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Command command, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(Integer.BYTES + command.getHeader().length() + command.getPayload().length);
        encodeHeader(ctx, command.getHeader(), byteBuf);
        byteBuf.writeBytes(command.getPayload());
    }

    protected void encodeHeader(ChannelHandlerContext channelHandlerContext, Header header, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(header.getType());
        byteBuf.writeInt(header.getVersion());
        byteBuf.writeInt(header.getRequestId());
    }
}
