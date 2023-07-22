package com.solidcode.gameprovider.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResponse {

  private long id;
  private String name;
  private boolean active;
  private String createdDate;
}
