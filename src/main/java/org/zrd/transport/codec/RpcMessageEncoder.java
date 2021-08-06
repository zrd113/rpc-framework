package org.zrd.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.zrd.dto.RpcMessage;
import org.zrd.serialize.Kyro.KryoSerializer;
import org.zrd.serialize.Serializer;
import org.zrd.transport.constants.RpcConstants;

/**
 * @Description netty编码器
 * @Author ZRD
 * @Date 2021/8/4
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf out) throws Exception {
        out.writeBytes(RpcConstants.MAGIC_NUMBER);
        out.writeByte(RpcConstants.VERSION);
        out.writerIndex(out.writerIndex() + 4);
        byte messageType = rpcMessage.getMessageType();
        out.writeByte(messageType);
        out.writeByte(rpcMessage.getCodec());
        //压缩方式
        out.writeByte((byte)1);
        byte[] body = null;
        int fullLength = RpcConstants.HEAD_LENGTH;

        Serializer serializer = new KryoSerializer();
        body = serializer.serialize(rpcMessage.getData());
        fullLength += body.length;

        if (body != null) {
            out.writeBytes(body);
        }

        int writeIndex = out.writerIndex();
        out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
        out.writeInt(fullLength);
        out.writerIndex(writeIndex);
    }
}
