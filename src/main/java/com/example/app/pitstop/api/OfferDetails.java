package com.example.app.pitstop.api;

import com.example.app.refdata.api.OperatorId;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.common.search.Facet;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class OfferDetails {
    @NotNull
    @Facet
    OperatorId operatorId;
    @NotNull @PositiveOrZero
    BigDecimal price;

    @AssertLegal
    void assertAuthorized(Sender sender) {
        if (!sender.isAuthorizedFor(operatorId)) {
            throw new IllegalCommandException("Not authorized for operator");
        }
    }
}
