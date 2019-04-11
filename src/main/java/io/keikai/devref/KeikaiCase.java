package io.keikai.devref;

/**
 * Each {@link KeikaiCase} demonstrates an API use case. You should call an KeikaiCase with such order: <br/>
 * <code>
 * keikaiApp.init(); <br/>
 * keikaiApp.run();
 * </code>
 */
public interface KeikaiCase {

    /**
     * connect to keikai engine. Initialize an application related data.
     * @param keikaiEngineAddress
     */
    void init(String keikaiEngineAddress);

    /**
     * get keikai UI client URL
     * @param domId
     * @return
     */
    String getJavaScriptURI(String domId);

    /**
     * run the main logic
     */
    void run();
}
