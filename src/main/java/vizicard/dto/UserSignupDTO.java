package vizicard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSignupDTO {
  // При регистрации человек отправляет username, пароль, имя и должность
  @ApiModelProperty(position = 0)
  private String username;
  @ApiModelProperty(position = 1)
  private String password;
  @ApiModelProperty(position = 2)
  private String name;
  @ApiModelProperty(position = 3)
  private String position;

}
