package org.spring.springboot.utils.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayOutputStream;


/**
 * @author luckylau
 * @date 2017/12/13/013 10:14
 */
public class KryoRedisSerializer<T> implements RedisSerializer<T> {
    private static final Logger logger = LoggerFactory.getLogger(KryoRedisSerializer.class);

    private static final int MAX_CAPACITY = 64 * 1024;

    private static final int COMPRESS_THRESHOLD = 1024;

    private KryoPool getKryoPool(){
        KryoFactory kryoFactory = new KryoFactory() {
            @Override
            public Kryo create() {
                Kryo kryo = new Kryo();
                kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
                return kryo;
            }
        };
        //softReferences防止内存溢出
        return new KryoPool.Builder(kryoFactory).softReferences().build();
    }

/* 这样实现也可以
   @Override
    public byte[] serialize(T t) throws SerializationException {
        KryoPool kryoPool = getKryoPool();
        Kryo kryo = kryoPool.borrow();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Output output = new Output(stream);
        kryo.writeClassAndObject(output, t);
        output.close();
        kryoPool.release(kryo);
        return stream.toByteArray();
    }*/

    @Override
    public byte[] serialize(final T t) throws SerializationException {
        return getKryoPool().run(new KryoCallback<byte[]>() {
            @Override
            public byte[] execute(Kryo kryo){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Output output = new Output(stream);
                kryo.writeClassAndObject(output, t);
                output.close();
                return stream.toByteArray();
            }
        });
    }
/*
    这样实现也可以
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if(bytes == null || bytes.length == 0){
            return null;
        }
        KryoPool kryoPool = getKryoPool();
        Kryo kryo = kryoPool.borrow();
        Input input = new Input(bytes);
        T o = (T)kryo.readClassAndObject(input);
        input.close();
        kryoPool.release(kryo);
        return o;
    }*/

    @Override
    public T deserialize(final byte[] bytes) throws SerializationException {
        if(bytes == null || bytes.length == 0){
            return null;
        }

        return getKryoPool().run(new KryoCallback<T>() {
            @Override
            public T execute(Kryo kryo){
                Input input = new Input(bytes);
                T o = (T)kryo.readClassAndObject(input);
                input.close();
                return o;
            }
        });
    }
}
