package rentconfigservice.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class PageDto<T> {

    private Integer number;

    private Integer size;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonProperty("total_elements")
    private Long totalElements;

    private Boolean first;

    @JsonProperty("number_of_elements")
    private Integer numberOfElements;

    private Boolean last;
    @JsonProperty("content")
    private List<T> content;

}
