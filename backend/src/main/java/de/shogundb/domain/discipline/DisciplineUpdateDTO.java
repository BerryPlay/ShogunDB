package de.shogundb.domain.discipline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class DisciplineUpdateDTO {
    @NotNull
    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    private String name;
}
