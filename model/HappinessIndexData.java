package com.thread_exec.thread_executor.model;

import com.univocity.parsers.annotations.Parsed;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class HappinessIndexData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Parsed(field = "countryName")
    private String countryName;

    @Parsed(field = "regionalIndicator")
    private String regionalIndicator;

    @Parsed(field = "ladderScore")
    private float ladderScore;

    @Parsed(field = "upperWhisker")
    private float upperWhisker;

    @Parsed(field = "lowerWhisker")
    private float lowerWhisker;

    @Parsed(field = "gdpPerCapita")
    private float gdpPerCapita;

    @Parsed(field = "socialSupports")
    private float socialSupports;

    @Parsed(field = "healthyLifeExpectancy")
    private float healthyLifeExpectancy;

    @Parsed(field = "freedomToMakeChoice")
    private float freedomToMakeChoice;

    @Parsed(field = "generosity")
    private float generosity;

    @Parsed(field = "perceptionsOfCorruptions")
    private float perceptionsOfCorruptions;

    @Parsed(field = "dystopiaResiduals")
    private float dystopiaResiduals;

}
