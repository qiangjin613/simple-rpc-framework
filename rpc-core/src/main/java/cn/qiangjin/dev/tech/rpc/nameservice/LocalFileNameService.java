package cn.qiangjin.dev.tech.rpc.nameservice;

import cn.qiangjin.dev.tech.rpc.NameService;
import cn.qiangjin.dev.tech.rpc.serialize.SerializeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 注册中心的实现类。
 * <p>
 * 实现逻辑：去读写一个本地文件，实现注册服务 registerService 方法时，把服务提供者保存到本地文件中；
 * 实现查找服务 lookupService 时，就是去本地文件中读出所有的服务提供者，找到对应的服务提供者，然后返回。
 *
 * @author <a href="mailto:qiangjin613@163.com">qiangjin</a>
 */
public class LocalFileNameService implements NameService {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileNameService.class);
    private static final Collection<String> SCHEMES = Collections.singleton("file");
    private File file;

    @Override
    public void registerService(String serviceName, URI uri) throws IOException {
        logger.info("Register service: {}, uri: {}.", serviceName, uri);
        try (
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                FileChannel fileChannel = randomAccessFile.getChannel()
        ) {
            // 使用操作系统级别的文件锁，将元数据写入文件中
            FileLock lock = fileChannel.lock();
            try {
                int fileLength = (int) randomAccessFile.length();
                Metadata metadata;
                byte[] bytes;
                if (fileLength > 0) {
                    bytes = new byte[(int) randomAccessFile.length()];
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    while (buffer.hasRemaining()) {
                        fileChannel.read(buffer);
                    }
                    metadata = SerializeSupport.parse(bytes);
                } else {
                    metadata = new Metadata();
                }
                List<URI> uris = metadata.computeIfAbsent(serviceName, k -> new ArrayList<>());
                if (!uris.contains(uri)) {
                    uris.add(uri);
                }
                logger.info(metadata.toString());

                bytes = SerializeSupport.serialize(metadata);
                fileChannel.truncate(bytes.length);
                fileChannel.position(0L);
                fileChannel.write(ByteBuffer.wrap(bytes));
                fileChannel.force(true);
            } finally {
                lock.release();
            }
        }
    }

    @Override
    public URI lookupService(String serviceName) throws IOException {
        Metadata metadata;
        try (
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                FileChannel fileChannel = randomAccessFile.getChannel()
        ){
            FileLock lock = fileChannel.lock();
            try {
                byte [] bytes = new byte[(int) randomAccessFile.length()];
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                while (buffer.hasRemaining()) {
                    fileChannel.read(buffer);
                }
                metadata = bytes.length == 0? new Metadata(): SerializeSupport.parse(bytes);
                logger.info(metadata.toString());
            } finally {
                lock.release();
            }
        }
        List<URI> uris = metadata.get(serviceName);
        if (null == uris || uris.isEmpty()) {
            return null;
        } else {
            // 随机获取一个地址返回
            return uris.get(ThreadLocalRandom.current().nextInt(uris.size()));
        }
    }

    @Override
    public void connect(URI nameServiceUri) {
        if (SCHEMES.contains(nameServiceUri.getScheme())) {
            file = new File(nameServiceUri);
        } else {
            throw new RuntimeException("Unsupported scheme!");
        }
    }

    @Override
    public Collection<String> supportedSchemes() {
        return SCHEMES;
    }
}
