package com.example.stockify.alertaStock.domain;

import lombok.Getter;

/* Evento que se publica cuando se crea una nueva alerta de stock. */
@Getter
public class AlertaStockCreadaEvent {
    private final AlertaStock alertaStock;

    public AlertaStockCreadaEvent(AlertaStock alertaStock) {
        this.alertaStock = alertaStock;
    }
}
