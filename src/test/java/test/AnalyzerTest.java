package test;

import io.keikai.devref.analyzer.Analyzer;
import org.junit.*;
import org.slf4j.*;

import java.io.IOException;

public class AnalyzerTest {

    @Test
    public void testStyle() throws IOException {
        String[] args = {"src/main/webapp/WEB-INF/books/cellStyle.xlsx"};
        Analyzer.main(args);
        Assert.assertEquals(17, Analyzer.getBookCounter().getCounter());
        Assert.assertEquals(1, Analyzer.getSheetCounter().getCounter());
        Assert.assertEquals(279, Analyzer.getCellCounter().getCounter());
    }

    private static final Logger logger = LoggerFactory.getLogger(Analyzer.class);
    @Test
    public void testLogger(){
        logger.debug("log" + "1st" + new Data());
        logger.debug("log 2 {} {}", "1st" , new Data());
        logger.debug("log 3 {} {} {}", "1st" , new Data(), "3rd");
        logger.debug("log 2 {} {}", new Object[]{"1st", new Data()});
    }

    static class Data{
        public Data(){
            System.out.println("new data");
        }

        @Override
        public String toString() {
            System.out.println("data tostring");
            return super.toString();
        }
    }

}
