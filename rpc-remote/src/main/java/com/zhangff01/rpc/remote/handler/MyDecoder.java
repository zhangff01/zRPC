package com.zhangff01.rpc.remote.handler;

import com.zhangff01.rpc.common.util.ProtoStuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author zhangfeifei
 * @Description 解码器 byte -> object
 * @create 2019/12/17
 */
public class MyDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public MyDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
        }
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        Object obj = ProtoStuffUtil.deserialize(data, genericClass);
        list.add(obj);
    }
}
