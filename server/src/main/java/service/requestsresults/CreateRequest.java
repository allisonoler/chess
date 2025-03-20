package service.requestsresults;

public record CreateRequest(
        String authToken,
        String gameName){
}