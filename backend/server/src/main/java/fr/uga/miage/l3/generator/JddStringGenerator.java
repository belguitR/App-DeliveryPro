package fr.uga.miage.l3.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class JddStringGenerator implements IdentifierGenerator {
    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return "jdd" + counter.getAndIncrement();
    }
}