package org.als.random.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionalClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalClass.class);

    public FunctionalClass() {
    }

    public <T> void addInfo(List<String> messageList, String key, Supplier<T> method) {
        if(Objects.isNull(messageList) || key.isEmpty() || Objects.isNull(method) )
            return;

        T value = null;
        try {
            value = method.get();
            messageList.add(String.format("  %s: %s", key, String.valueOf(value)));
        } catch (Exception e) {
            messageList.add(String.format("  %s: %s", key, e.getMessage()));
        }
    }

    /*
     *  USAGE:
     *         getStringValue( () -> (new FunctionalClass().sayHello()) )
     **/
    public <T> String getStringValue( Supplier<T> method) {
        String value = "ERROR: {msg: method not evaluated.";
        if( Objects.isNull(method) )
            return value;

        try {
            value = String.valueOf(method.get());
        } catch (Exception e) {
            try {
                value = String.format("ERROR at getStringValue: [method: %s, error: %s]", method.toString(), e.getMessage());
            }catch( Exception ex ){
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        return value;
    }

    /*
     *  USAGE:
     *         getStringValue( new FunctionalClass(), FunctionalClass::sayYourAge )
     **/
    public <T, R> String getStringValue(T obj, Function<T, R> method) {
        String value = "ERROR: {msg: method not evaluated.";
        if(Objects.isNull(obj) || Objects.isNull(method) )
            return value;

        try {
            value = String.valueOf( method.apply(obj).toString() );
        } catch (Exception e) {
            value = e.getMessage();
        }

        return value;
    }

    private List<String> getFormatedStackTrace( int identSpaces ) {
        List<String> messageLinesList = new ArrayList<>();
        try {
            List<String> noPrintableMethods = Arrays.asList("getStackTrace", "getFormatedStackTrace");

            String titleIdent = String.format( "%"+identSpaces+"s", " " );
            String stackIdent = String.format("%"+(identSpaces+2)+"s", " ");

            messageLinesList.add(String.format("%scall stacktrace: [", titleIdent));

            for( StackTraceElement stack : Thread.currentThread().getStackTrace()) {
                if( !noPrintableMethods.contains(stack.getMethodName()) )
                    messageLinesList.add(String.format( "%s%s", stackIdent, stack ));
            }
            messageLinesList.add(String.format("%s]", titleIdent));
        } catch( Exception e ) {
            try {
                messageLinesList.add(String.format("ERROR at getFormatedStackTrace method: ", e.getMessage()));
            } catch( Exception ex ) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        return messageLinesList;
    }

    public String sayHello(){
        return "Hello";
    }
    public int sayYourAge(){
        return 38;
    }

    public static void main( String[] args ){
        FunctionalClass func = new FunctionalClass();

        LOGGER.info( func.getStringValue( () -> (new FunctionalClass().sayHello()) ));
        LOGGER.info(String.format("sayHello: %s", func.getStringValue( new FunctionalClass(), FunctionalClass::sayHello )));
        LOGGER.info(String.format("sayYourAge: %s", func.getStringValue( new FunctionalClass(), FunctionalClass::sayYourAge )));
        LOGGER.info(String.join(System.lineSeparator(), func.getFormatedStackTrace(2)));

    }
}
