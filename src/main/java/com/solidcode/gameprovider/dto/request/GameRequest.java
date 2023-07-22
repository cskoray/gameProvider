package com.solidcode.gameprovider.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameRequest {

  @NotNull
  @Size(min = 3, max = 35)
  private String name;

  @NotNull
  private boolean active;
}
