package com.bakirwebservice.invoiceservice.websocket.controller;

import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.api.response.InvoiceStatusUpdate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class InvoiceWebSocketController {

    @MessageMapping("/invoice-status")
    @SendTo("/topic/status-updates")
    public InvoiceStatusUpdate sendStatusUpdate(InvoiceRequest request){
        /*return new InvoiceStatusUpdate(
                request.getRequestId(),
                request.getStatus(),
                request.getEstimatedCompletionTime()
        );*/
        return null;
    }

}
