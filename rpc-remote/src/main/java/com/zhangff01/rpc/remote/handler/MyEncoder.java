package com.zhangff01.rpc.remote.handler;

import com.zhangff01.rpc.common.ProtoStuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zhangfeifei
 * @Description 编码器 object -> byte
 * @create 2019/12/17
 */
public class MyEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    public MyEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          Object in, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = ProtoStuffUtil.serialize(in);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}
