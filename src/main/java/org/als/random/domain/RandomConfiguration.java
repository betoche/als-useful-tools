package org.als.random.domain;

import lombok.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RandomConfiguration {
    private String name;
    private List<String> spinnerClasses;
    private List<Option> genericOptions;
    private List<Option> specificOptions;

    public String getRandomSpinnerClass() {
        return spinnerClasses.get( (int)(Math.random() * spinnerClasses.size()) );
    }

    public String toString() {
        return String.format("RandomConfiguration: { name: %s, genericOptions: %s, specificOptions: %s }", getName(), getGenericOptions(), getSpecificOptions() );
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private String key;
        private String title;
        private String description;
        private String path;
        private List<Option> subOptions;

        public String toString() {
            return String.format("Option: {key: %s, title: %s, description: %s, path: %s}", getKey(), getTitle(), getDescription(), getPath());
        }

        public String toJsonString() {
            JSONObject json = new JSONObject();
            json.put("key", getKey());
            json.put("title", getTitle());
            json.put("description", getDescription());
            json.put("path", getPath());
            json.put("hasSubOptions", subOptions != null && !subOptions.isEmpty());

            return json.toString();
        }
    }

    public Option findOptionByKey( String key ) {
        for( Option opt : getGenericOptions() ){
            if( opt.getKey().equalsIgnoreCase(key) )
                return opt;
        }

        for( Option opt :  getSpecificOptions() ){
            if( opt.getKey().equalsIgnoreCase(key) )
                return opt;
        }

        return null;
    }
}
