package com.pirimid.cryptotrade.util;

import com.google.gson.Gson;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderResDTO;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.request.PlaceOrderReqCoinbaseDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.PlaceOrderResCoinbaseDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.SymbolResCoinbaseDTO;
import com.pirimid.cryptotrade.model.OrderType;
import com.pirimid.cryptotrade.model.Side;
import com.pirimid.cryptotrade.model.Status;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Component
public class CoinbaseUtil {


   public static String getSignature(String timestamp,String secretKeyString,String method,String path,String body) throws NoSuchAlgorithmException, InvalidKeyException {
        String prehash = timestamp+method+path+body;
        byte[] secretKeyDecoded = Base64.getDecoder().decode(secretKeyString);
        SecretKey secretKey = new SecretKeySpec(secretKeyDecoded, "HmacSHA256");
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(secretKey);
        String sin =  Base64.getEncoder().encodeToString(hmacSha256.doFinal(prehash.getBytes()));

       return sin;
    }

    public static PlaceOrderResDTO getPlaceOrderResDTO(PlaceOrderResCoinbaseDTO placeOrderResCoinbaseDTO){
        PlaceOrderResDTO placeOrderResDTO;
        Gson gson = new Gson();
        String orderCoinbaseres = gson.toJson(placeOrderResCoinbaseDTO);
        placeOrderResDTO = gson.fromJson(orderCoinbaseres,PlaceOrderResDTO.class);
        placeOrderResDTO.setExecutedAmount(placeOrderResCoinbaseDTO.getExecuted_value());
        placeOrderResDTO.setType(OrderType.valueOf(placeOrderResCoinbaseDTO.getType().toUpperCase()));
        placeOrderResDTO.setSide(Side.valueOf(placeOrderResCoinbaseDTO.getSide().toUpperCase()));
        placeOrderResDTO.setStatus(Status.valueOf(placeOrderResCoinbaseDTO.getStatus().toUpperCase()));
        placeOrderResDTO.setCreatedAt(placeOrderResCoinbaseDTO.getCreated_at());
        placeOrderResDTO.setSymbol(placeOrderResCoinbaseDTO.getProduct_id());
        return placeOrderResDTO;
    }
    public static PlaceOrderReqCoinbaseDTO getPlaceOrderReqDTO(PlaceOrderReqDTO placeOrderReqDTO){
        PlaceOrderReqCoinbaseDTO placeOrderReqCoinbaseDTO;
        Gson gson = new Gson();
        String orderCoinbasereq = gson.toJson(placeOrderReqDTO);
        placeOrderReqCoinbaseDTO = gson.fromJson(orderCoinbasereq,PlaceOrderReqCoinbaseDTO.class);
        placeOrderReqCoinbaseDTO.setType(placeOrderReqDTO.getType().getValue());
        placeOrderReqCoinbaseDTO.setSide(placeOrderReqDTO.getSide().getValue());
        placeOrderReqCoinbaseDTO.setProduct_id(placeOrderReqDTO.getSymbol());
        return placeOrderReqCoinbaseDTO;
   }

    public static List<SymbolResDTO> getPairsResDTO(List<SymbolResCoinbaseDTO> symbolResCoinbaseDTOS){
        List<SymbolResDTO> symbolResDTOS = new ArrayList<>();
        for(SymbolResCoinbaseDTO symbol:symbolResCoinbaseDTOS) {
            if(symbol != null) {
                SymbolResDTO symbolResDTO = new SymbolResDTO(symbol.getId(),symbol.getBase_currency(),symbol.getQuote_currency(),symbol.getBase_min_size(),symbol.getMin_market_funds());
                symbolResDTOS.add(symbolResDTO);
            }
        }
        return symbolResDTOS;

    }


}
