package com.thetapay.ussdapp.controller;

import com.thetapay.ussdapp.apiModels.*;
import com.thetapay.ussdapp.entity.Profile;
import com.thetapay.ussdapp.repository.ProfileRepository;
import com.thetapay.ussdapp.thetaRestClient.ThetaRestClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UssdController {

    private static final String Theta_CLI_URL = "https://api.infstones.com/theta/mainnet/1b398b059a5244ea9588d7c9ce795fa0/thetacli/rpc";
    private static final String Theta_URL = "https://api.infstones.com/theta/mainnet/1b398b059a5244ea9588d7c9ce795fa0/theta/rpc";

    private final ProfileRepository profileRepository;

    public UssdController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @PostMapping("/ussd")
    public String handleUssdRequest(@RequestBody String requestBody) throws UnsupportedEncodingException {
        // Read the variables sent via POST from our API
        System.out.println("request body: " + requestBody);
        Map<String, String> body = Arrays.stream(requestBody.split("&"))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(entry -> entry[0], entry -> entry.length == 2 ? entry[1] : ""));

        String sessionId = body.get("sessionId");
        String serviceCode = body.get("serviceCode");
        String phoneNumber = URLDecoder.decode(body.get("phoneNumber"), StandardCharsets.UTF_8);
        String text = body.get("text");
        String[] inputArr = text.split("\\*");

        StringBuilder ussdAppResponse = new StringBuilder("");

        if (text.isEmpty()) {
            if(profileRepository.existsByPhoneNumber(phoneNumber)){
                ussdAppResponse.append("CON ThetaPay USSD APP\n\n");
                ussdAppResponse.append("1. Check Your Theta Address\n");
                ussdAppResponse.append("2. Balance\n");
                ussdAppResponse.append("3. Transfer\n");
                ussdAppResponse.append("4. Fund Theta Address\n");
            }else {
                ussdAppResponse.append("CON Welcome to ThetaPay USSD APP\n\n");
                ussdAppResponse.append("1. Register\n");
            }
        } else if (text.equals("1")) {
            if(profileRepository.existsByPhoneNumber(phoneNumber)){
                String thetaAddress = profileRepository.findThetaAddressByPhoneNumber(phoneNumber);
                ussdAppResponse.append("END Your Theta Address is:\n");
                ussdAppResponse.append(printLongTextOnUSSD(thetaAddress));
            }
            else {
                NewKeyRequest newKeyRequest = new NewKeyRequest();
                newKeyRequest.setJsonrpc("2.0");
                newKeyRequest.setMethod("thetacli.NewKey");
                newKeyRequest.setId(1);
                NewKeyRequest.Params param = new NewKeyRequest.Params();
                param.setPassword("qwertyuiop");
                newKeyRequest.setParams(Collections.singletonList(param));
                NewKeyResponse newKeyResponse = ThetaRestClient.post(Theta_CLI_URL, newKeyRequest, NewKeyResponse.class);
                System.out.println(newKeyResponse.toString());

                UnlockKeyRequest unlockKeyRequest = new UnlockKeyRequest();
                unlockKeyRequest.setJsonrpc("2.0");
                unlockKeyRequest.setMethod("thetacli.UnlockKey");
                unlockKeyRequest.setId(1);
                UnlockKeyRequest.Params unlockKeyParam = new UnlockKeyRequest.Params();
                unlockKeyParam.setPassword("qwertyuiop");
                unlockKeyParam.setAddress(newKeyResponse.getResult().getAddress());
                unlockKeyRequest.setParams(Collections.singletonList(unlockKeyParam));
                UnlockKeyResponse unlockKeyResponse = ThetaRestClient.post(Theta_CLI_URL,unlockKeyRequest,UnlockKeyResponse.class);
                System.out.println(unlockKeyResponse.toString());

                Profile profile = new Profile();
                profile.setBalance(0);
                profile.setPhoneNumber(phoneNumber);
                profile.setThetaAddress(newKeyResponse.getResult().getAddress());
                profile.setCreatedAt(LocalDateTime.now());
                profileRepository.save(profile);

                ussdAppResponse.append("END Registration Successful, Account Details: \n\n")
                        .append("Phone: ").append(phoneNumber).append("\n")
                        .append("Theta Address: \n").append(printLongTextOnUSSD(newKeyResponse.getResult().getAddress()));
            }
        } else if (text.equals("2")) {
            Profile profile = profileRepository.findProfileByPhoneNumber(phoneNumber);
            if(profile != null){
                GetAccountRequest getAccountRequest = new GetAccountRequest();
                getAccountRequest.setJsonrpc("2.0");
                getAccountRequest.setMethod("theta.GetAccount");
                getAccountRequest.setId(1);
                GetAccountRequest.Params params = new GetAccountRequest.Params();
                params.setAddress(profile.getThetaAddress());
                getAccountRequest.setParams(Collections.singletonList(params));

                GetAccountResponse getAccountResponse = ThetaRestClient.post(Theta_URL,getAccountRequest,GetAccountResponse.class);
                System.out.println(getAccountResponse.toString());
                double balance = profile.getBalance();
                ussdAppResponse.append("END Your balance is: \n\n");
                ussdAppResponse.append(printLongTextOnUSSD("thetawei: "+"994999990000000000000000000"+"\n\n"));
                ussdAppResponse.append(printLongTextOnUSSD("tfuelwei: "+"4999999979999999000000000000"));
            }
        } else if (text.equals("3")) {
            // make the transfer
            ussdAppResponse.append("CON Enter recipient phone number: \n");
        }else if(text.startsWith("3*") && inputArr.length == 2){
            String toPhoneNumber = URLDecoder.decode(inputArr[1], StandardCharsets.UTF_8);
            if (!profileRepository.existsByPhoneNumber(toPhoneNumber)){
                return ussdAppResponse.append("END Phone number entered does not belong to a registered customer").toString();
            }else {
                ussdAppResponse.append("CON Enter amount of Theta You want to transfer: \n");
            }
        }else if(text.startsWith("3*") && inputArr.length == 3){
            String toPhoneNumber = URLDecoder.decode(inputArr[1], StandardCharsets.UTF_8);
            String amountOfTheta = inputArr[2];
            String amountOfThetaWei = amountOfTheta + "000000000000000000";
            String fromAddress = profileRepository.findThetaAddressByPhoneNumber(phoneNumber);
            String toAddress = profileRepository.findThetaAddressByPhoneNumber(toPhoneNumber);
            SendRequest sendRequest = new SendRequest();
            sendRequest.setJsonrpc("2.0");
            sendRequest.setMethod("thetacli.Send");
            sendRequest.setId(1);
            SendRequest.Params param = new SendRequest.Params();
            param.setChainId("privatenet");
            param.setFrom(fromAddress);
            param.setTo(toAddress);
            param.setThetaWei(amountOfThetaWei);
            param.setTFuelWei("0");
            param.setFee("1000000000000");
            param.setSequence("6");
            param.setAsync(false);
            sendRequest.setParams(Collections.singletonList(param));
            SendResponse sendResponse = ThetaRestClient.post(Theta_CLI_URL,sendRequest,SendResponse.class);
            System.out.println(sendResponse.toString());
            ussdAppResponse.append("END Transaction successful").append("\n\n");
            ussdAppResponse.append("From phone: ").append(phoneNumber).append("\n");
            ussdAppResponse.append("From Address: \n").append(printLongTextOnUSSD(fromAddress)).append("\n");
            ussdAppResponse.append("To phone: ").append(toPhoneNumber).append("\n");
            ussdAppResponse.append("To Address: \n").append(printLongTextOnUSSD(toAddress)).append("\n");
            ussdAppResponse.append("Theta Sent: ").append(amountOfTheta);
        }else if(text.equals("4")){
            ussdAppResponse.append("CON Enter amount of Theta You want to fund your account with: \n");
        }else if(text.startsWith("4*")){
            ussdAppResponse.append("END You will get generated bank details via SMS to transfer to. \n");
            ussdAppResponse.append("Your theta address will be credited automatically \n");
        }

        if (ussdAppResponse.toString().isBlank()){
            ussdAppResponse.append("END Incorrect Input \n");
        }
        return ussdAppResponse.toString();
    }

    private String printLongTextOnUSSD(String thetaAddress){
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (char c : thetaAddress.toCharArray()) {
            sb.append(c);
            count++;
            if (count % 21 == 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}