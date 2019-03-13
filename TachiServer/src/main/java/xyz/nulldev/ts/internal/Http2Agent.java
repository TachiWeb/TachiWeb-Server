package xyz.nulldev.ts.internal;

import com.ea.agentloader.AgentLoader;
import org.mortbay.jetty.alpn.agent.Premain;

import java.lang.instrument.Instrumentation;

/**
 * Class used to install the ALPN agent
 * <p>
 * The ALPN agent is used to inject ALPN capability into Java versions <= 1.8
 */
public final class Http2Agent {
    private Http2Agent() {
    }

    public static void install() {
        AgentLoader.loadAgentClass(Http2Agent.class.getName(), "");
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        Premain.premain(args, inst);
    }
}