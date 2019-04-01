package xyz.nulldev.ts.internal;

import com.ea.agentloader.AgentLoader;
import org.mortbay.jetty.alpn.agent.Premain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

/**
 * Class used to install the ALPN agent
 * <p>
 * The ALPN agent is used to inject ALPN capability into Java versions <= 1.8
 */
public final class Http2Agent {
    private static Logger logger = LoggerFactory.getLogger(Http2Agent.class);

    private Http2Agent() {
    }

    public static void install() {
        logger.info("Installing ALPN agent...");
        try {
            AgentLoader.loadAgentClass(Http2Agent.class.getName(), "");
        } catch (Throwable t) {
            logger.warn("Failed to dynamically install ALPN agent!", t);
        }
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        try {
            Premain.premain(args, inst);
        } catch (Throwable t) {
            logger.warn("ALPN agent failed to boot!", t);
        }
    }
}