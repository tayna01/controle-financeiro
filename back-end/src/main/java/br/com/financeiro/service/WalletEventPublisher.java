package br.com.financeiro.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.financeiro.event.TransactionChangedEvent;

@Service
public class WalletEventPublisher {

    private final Map<Long, CopyOnWriteArrayList<org.springframework.web.servlet.mvc.method.annotation.SseEmitter>> emitters =
            new ConcurrentHashMap<>();

    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter subscribe(Long walletId) {
        org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter =
                new org.springframework.web.servlet.mvc.method.annotation.SseEmitter(0L);

        emitters.computeIfAbsent(walletId, key -> new CopyOnWriteArrayList<>()).add(emitter);

        Runnable cleanup = () -> remove(walletId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(error -> cleanup.run());

        try {
            emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event()
                    .name("connected")
                    .data("connected"));
        } catch (IOException ex) {
            cleanup.run();
        }

        return emitter;
    }

    public void publish(TransactionChangedEvent event) {
        List<org.springframework.web.servlet.mvc.method.annotation.SseEmitter> list = emitters.get(event.getWalletId());
        if (list == null) {
            return;
        }

        for (org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter : list) {
            try {
                emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event()
                        .name("transaction-changed")
                        .data(event));
            } catch (Exception ex) {
                remove(event.getWalletId(), emitter);
            }
        }
    }

    @Scheduled(fixedRate = 25000)
    public void heartbeat() {
        for (Map.Entry<Long, CopyOnWriteArrayList<org.springframework.web.servlet.mvc.method.annotation.SseEmitter>> entry : emitters.entrySet()) {
            for (org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter : entry.getValue()) {
                try {
                    emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event()
                            .comment("ping"));
                } catch (Exception ex) {
                    remove(entry.getKey(), emitter);
                }
            }
        }
    }

    private void remove(Long walletId, org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter) {
        List<org.springframework.web.servlet.mvc.method.annotation.SseEmitter> list = emitters.get(walletId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                emitters.remove(walletId);
            }
        }
    }
}