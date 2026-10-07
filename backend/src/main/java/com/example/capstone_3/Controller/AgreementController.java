package com.example.capstone_3.Controller;

import com.example.capstone_3.Api.ApiResponse;
import com.example.capstone_3.DtoIn.AgreementDtoIn;
import com.example.capstone_3.Service.AgreementService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agreement")
public class AgreementController {

    private final AgreementService agreementService;
    private final com.example.capstone_3.Service.ExchangeService exchangeService;
    private final com.example.capstone_3.Service.AccountAccessService accessService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.status(200).body(agreementService.get());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody @Valid AgreementDtoIn agreementDtoIn, HttpSession session) {
        Integer accountId = (Integer) session.getAttribute("accountId");
        if (!"ADMIN".equals(accessService.requireActive(accountId).getAccountType())) {
            exchangeService.getExchangeDetails(accountId, agreementDtoIn.getExchangeId());
        }
        agreementService.add(agreementDtoIn);
        return ResponseEntity.status(200).body(new ApiResponse("agreement added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid AgreementDtoIn agreementDtoIn, HttpSession session) {
        if (!id.equals(agreementDtoIn.getExchangeId())) throw new com.example.capstone_3.Api.ApiException("Agreement cannot be moved to another exchange");
        Integer accountId = (Integer) session.getAttribute("accountId");
        if (!"ADMIN".equals(accessService.requireActive(accountId).getAccountType())) {
            exchangeService.getExchangeDetails(accountId, agreementDtoIn.getExchangeId());
        }
        agreementService.update(id, agreementDtoIn);
        return ResponseEntity.status(200).body(new ApiResponse("agreement updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        agreementService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("agreement deleted"));
    }

    @PutMapping("/{exchangeId}/provider-accept")
    public ResponseEntity<?> providerAccept(@PathVariable Integer exchangeId, HttpSession session) {
        agreementService.providerAccept((Integer) session.getAttribute("accountId"), exchangeId);
        return ResponseEntity.status(200).body(new ApiResponse("Provider accepted the agreement"));
    }

    @PutMapping("/{exchangeId}/receiver-accept")
    public ResponseEntity<?> receiverAccept(@PathVariable Integer exchangeId, HttpSession session) {
        agreementService.receiverAccept((Integer) session.getAttribute("accountId"), exchangeId);
        return ResponseEntity.status(200).body(new ApiResponse("Receiver accepted the agreement"));
    }

    @GetMapping("/{exchangeId}/status")
    public ResponseEntity<?> getAcceptanceStatus(@PathVariable Integer exchangeId, HttpSession session) {
        return ResponseEntity.status(200).body(agreementService.getAcceptanceStatus((Integer) session.getAttribute("accountId"), exchangeId));
    }
}


