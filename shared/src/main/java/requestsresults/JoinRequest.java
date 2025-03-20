package requestsresults;

public record JoinRequest(
        String authToken,
        String playerColor,
        String gameID){

}