package com.thread_exec.thread_executor.Utils;

import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvRoutines;
import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Log4j
public class FileUtils {

    /**
     * This method reads data from csv file and output the Iterable collection
     * Iterable is used to optimise memory usage
     * @param filePath
     * @param className className: The class type into which the CSV rows should be converted.
     * @param <T> A generic type parameter indicating that the method works with any type.
     * @return  An iterable collection of type T, optimized for memory usage.
     */

    public <T>IterableResult<T, ParsingContext> readDataFromCSV(String filePath, Class<T> className) throws IOException {
        InputStreamReader reader = null;
        if (StringUtils.isEmpty(filePath) && className == null){
            log.info("filePath or className is empty or invalid ");
            log.info("filePath : "+ filePath+" className : "+ null);
            return null;
        }
       try{
//           This object holds configuration settings for the CSV parser.
           CsvParserSettings settings = new CsvParserSettings();
//           Configures the parser to use the system's line separator.
           settings.getFormat().setLineSeparator(System.lineSeparator());
           reader = new InputStreamReader(new FileInputStream(filePath));
//           Uses CsvRoutines to read and parse the CSV file. It returns an IterableResult of type T and ParsingContext.
//           The iterate method is used to get an iterable result from the CSV file, which helps in optimizing memory usage.
           return new CsvRoutines(settings).iterate(className, reader);
       }catch (Exception e){
           log.error("An error occurred while reading the file from CSV file");
       }
       return null;
    }

    public int numberOfLines(String path) throws IOException {
        int count = 0;
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(path));
        try{
            byte[] c = new byte[1024];
            int readChars= 0;
            boolean empty = true;
            while ((readChars = inputStream.read(c))!= -1){
                empty = false;
                for (int i =0; i < readChars;++i){
                    if (c[i]=='\n'){
                        ++count;
                    }
                }
            }

            log.info("number of lines found in the file is "+ count);
            return (count==0 && !empty) ? 1 : count;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            inputStream.close();
        }
        log.info("number of lines found in the file is "+ count);
        return count;
    }
}
