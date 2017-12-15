package org.spring.springboot.utils.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;


/**
 * @author luckylau
 * @date 2017/12/13/013 10:14
 */
public class KryoRedisSerializer<T> implements RedisSerializer<T> {
    private static final Logger logger = LoggerFactory.getLogger(KryoRedisSerializer.class);

    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = new ThreadLocal<>();

    private static final int MAX_CAPACITY = 64 * 1024;

    private static final int COMPRESS_THRESHOLD = 1024;

    private Class<T> clazz;

    public KryoRedisSerializer(Class<T> clazz){this.clazz = clazz; }


    private Kryo getKryo(){
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        if(kryo == null){
            kryo = new Kryo();
            KRYO_THREAD_LOCAL.set(kryo);
        }
        return kryo;
    }

    private void removeKryo(){
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        if(kryo != null){
            KRYO_THREAD_LOCAL.remove();
        }
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        byte[] bytes = new byte[0];
        if( t == null){
            return bytes;
        }
        Output output = new Output(MAX_CAPACITY);
        try {
            this.getKryo().writeObject(output, t);
            bytes = output.toBytes();
        } catch (Exception e) {
            throw new SerializationException("Could not write kryo: "+ e.getMessage(),e);
        } finally {
            output.close();
            this.removeKryo();
        }
        return bytes;
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if(bytes == null || bytes.length == 0){
            return null;
        }

        Input input = new Input(bytes);
        try {
            return this.getKryo().readObject(input,clazz);
        } catch (Exception e) {
            throw new SerializationException("Could not read kryo: "+ e.getMessage(),e);
        } finally {
            input.close();
            this.removeKryo();
        }
    }
}
