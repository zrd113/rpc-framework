package org.zrd.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.zrd.compress.Compress;
import org.zrd.dto.RpcMessage;
import org.zrd.enums.CompressEnum;
import org.zrd.enums.SerializationEnum;
import org.zrd.serialize.Serializer;
import org.zrd.transport.constants.RpcConstants;
import org.zrd.utils.extension.ExtensionLoader;

/**
 *   0     1     2     3     4        5    6    7    8     9    10     11            12      13        14
 *   +-----+-----+-----+-----+--------+----+----+----+-----+-----+-----+-------------+-------+----------+
 *   |   magic   code        |version |    full length     |head length| messageType | codec | compress |
 *   +-----------------------+--------+--------------------+-----------+-------------+-------+----------+
 *   |                                        extended field                                            |
 *   +--------------------------------------------------------------------------------------------------+
 *   |                                                                                                  |
 *   |                                         body                                                     |
 *   |                                                                                                  |
 *   +--------------------------------------------------------------------------------------------------+
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
        out.writerIndex(out.writerIndex() + 6);
        out.writeByte(rpcMessage.getMessageType());
        out.writeByte(rpcMessage.getCodec());
        out.writeByte(rpcMessage.getCompress());
        byte[] body = null;
        int fullLength = RpcConstants.FIXED_HEAD_LENGTH;
        short headLength = RpcConstants.FIXED_HEAD_LENGTH;

        byte messageType = rpcMessage.getMessageType();
        if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            String codecName = SerializationEnum.getName(rpcMessage.getCodec());
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension(codecName);

            body = serializer.serialize(rpcMessage.getData());
            log.info("数据序列化完毕");

            String compressName = CompressEnum.getName(rpcMessage.getCompress());
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                    .getExtension(compressName);

            body = compress.compress(body);
            log.info("数据压缩完毕");
            fullLength = headLength + body.length;
        }

        if (body != null) {
            out.writeBytes(body);
        }

        int writeIndex = out.writerIndex();
        out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
        out.writeInt(fullLength);
        out.writeShort(headLength);
        out.writerIndex(writeIndex);
    }
}
