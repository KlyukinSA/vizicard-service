package vizicard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
public class UserUpdateDTO {
    // Пользователь может обновить свой профиль, изменяя необходимые поля, такие как
    // имя, должность, город, описание или компания
    @ApiModelProperty(position = 0)
    private String name;
    @ApiModelProperty(position = 1)
    private String position;
    @ApiModelProperty(position = 2)
    private String description;
    @ApiModelProperty(position = 3)
    private String company;
    @ApiModelProperty(position = 4)
    private String city;

}
