package vizicard.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.AppUserRole;

@Data
@NoArgsConstructor
public class UserDataDTO {
  
  @ApiModelProperty(position = 0)
  private String username;
//  @ApiModelProperty(position = 1)
//  private String email;
  @ApiModelProperty(position = 2)
  private String password;
  @ApiModelProperty(position = 3)
  private String name;
  @ApiModelProperty(position = 4)
  List<AppUserRole> appUserRoles;

}
