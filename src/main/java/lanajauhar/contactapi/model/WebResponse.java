package lanajauhar.contactapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse <T> {

    public T data;

    private String errorMessage;

    private PagingResponse paging;

}
