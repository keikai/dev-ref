package io.keikai.devref;

/**
 * Each {@link UseCase} demonstrates an API use case. You should call it with such order: <br/>
 * <code>
 * keikaiApp.init(); <br/>
 * keikaiApp.run();
 * </code>
 */
public interface UseCase {

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
