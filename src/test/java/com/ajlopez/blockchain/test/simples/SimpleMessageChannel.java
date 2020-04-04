package com.ajlopez.blockchain.test.simples;

import com.ajlopez.blockchain.net.MessageChannel;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.messages.Message;
//import javafx.util.Pair;

import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ajlopez on 19/11/2018.
 */
public class SimpleMessageChannel implements MessageChannel {
    private List<Consumer<Pair<Peer, Message>>> consumers = new ArrayList<>();
    private List<Pair<Peer, Message>> peerMessages = new ArrayList<>();

    public void postMessage(Peer peer, Message message) {
        Pair<Peer, Message> peerMessage = Pair.of(peer, message);
        this.peerMessages.add(peerMessage);
        this.consumers.forEach(consumer -> consumer.accept(peerMessage));
    }

    public void onMessage(Consumer<Pair<Peer, Message>> consumer) {
        this.consumers.add(consumer);
    }

    public List<Pair<Peer, Message>> getPeerMessages() {
        return this.peerMessages;
    }

    public Message getLastMessage() {
        if (this.peerMessages.isEmpty())
            return null;

        return this.peerMessages.get(this.peerMessages.size() - 1).getValue();
    }
}
