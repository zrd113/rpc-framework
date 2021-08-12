package org.zrd.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.zrd.compress.Compress;
import org.zrd.dto.RpcMessage;
import org.zrd.dto.RpcRequest;
import org.zrd.dto.RpcResponse;
import org.zrd.enums.CompressEnum;
import org.zrd.enums.SerializationEnum;
import org.zrd.serialize.Serializer;
import org.zrd.transport.constants.RpcConstants;
import org.zrd.utils.extension.ExtensionLoader;

import java.util.Arrays;

/**
 * @Description netty解码器
 * @Author ZRD
 * @Date 2021/8/4
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * @Description: 构造函数
     * @Param maxFrameLength      最大包长度，生成的数据包超过这个长度就会报错
     * @Param lengthFieldOffset   Length的偏移位
     * @Param lengthFieldLength   Length字段的长度
     * @Param lengthAdjustment    一般为负数，指的是Length前面数据的长度(包括Length)
     * @Param initialBytesToStrip 识别出整个数据包后，只要initialBytesToStrip起的数据
     * @Return:
     * @Date: 2021/8/6
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) decode;
            if (byteBuf.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeBuf(byteBuf);
                } catch (Exception e) {
                    log.error("数据解码出错", e);
                } finally {
                    byteBuf.release();
                }
            }
        }
        return decode;
    }

    private Object decodeBuf(ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .messageType(messageType)
                .compress(compressType)
                .build();

        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }

        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bytes = new byte[bodyLength];
            in.readBytes(bytes);

            String compressName = CompressEnum.getName(rpcMessage.getCompress());
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                    .getExtension(compressName);
            bytes = compress.decompress(bytes);

            log.info("数据解压缩完毕");

            String codecName = SerializationEnum.getName(rpcMessage.getCodec());
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension(codecName);

            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
                rpcMessage.setData(rpcRequest);
            } else {
                RpcResponse rpcResponse = serializer.deserialize(bytes, RpcResponse.class);
                rpcMessage.setData(rpcResponse);
            }
            log.info("数据反序列化完毕");
        }
        return rpcMessage;
    }

    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("version 不正确" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] mn = new byte[len];
        in.readBytes(mn);
        for (int i = 0; i < mn.length; i++) {
            if (mn[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new RuntimeException("magic number不正确" + Arrays.toString(mn));
            }
        }
    }
}