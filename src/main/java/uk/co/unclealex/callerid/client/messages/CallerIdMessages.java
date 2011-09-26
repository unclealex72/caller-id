package uk.co.unclealex.callerid.client.messages;

import java.util.Date;
import java.util.List;

import com.google.gwt.i18n.client.Messages;

public interface CallerIdMessages extends Messages {

	@DefaultMessage("Rokta - {0}")
	String title(String title);
	
	@DefaultMessage("Rokta - {0} for {1}")
	String gameFilterTitle(String title, String gameFilter);
	
	@DefaultMessage("{0} {1}")
	String gameFilter(String modifier, String unmodifiedGameFilter);
	
	@DefaultMessage("Current information")
	String information();

	@DefaultMessage("The league")
	String league();

	@DefaultMessage("The league graph")
	String graph();

	@DefaultMessage("Winning streaks")
	String winningStreaks();

	@DefaultMessage("Losing streaks")
	String losingStreaks();

	@DefaultMessage("Head to heads")
	String headToHeads();

	@DefaultMessage("Administration")
	String admin();

	@DefaultMessage("Get ready to play!")
	String newGame();

	@DefaultMessage("Oh, {0}, I''m so so sorry...")
	String gameLostOne(String loser);

	@DefaultMessage("And the loser is... {0}")
	String gameLostTwo(String loser);

	@DefaultMessage("{0} has lost")
	String gameLostThree(String loser);

	@DefaultMessage("{0} will be making the next round")
	String gameLostFour(String loser);

	@DefaultMessage("New game: round {0}")
	String gameRound(int round);		

	@DefaultMessage("")
	String all();

	@DefaultMessage("in {0,date,yyyy}")
	String year(Date date);

	@DefaultMessage("during the week starting on {0,date,d MMMM yyyy}")
	String week(Date date);

	@DefaultMessage("during {0,date,MMMM yyyy}")
	String month(Date date);

	@DefaultMessage("since {0,date,dd MMMM yyyy}")
	String since(Date date);

	@DefaultMessage("between {0,date,d MMMM yyyy} and {1,date,d MMMM yyyy}")
	String between(Date from, Date to);

	@DefaultMessage("before {0,date,d MMMM yyyy}")
	String before(Date date);

	@DefaultMessage("{0}''s profile")
	String profile(String username);

	@DefaultMessage("first games of the year")
	String firstGameOfTheYear();

	@DefaultMessage("first games of the month")
	String firstGameOfTheMonth();

	@DefaultMessage("first games of the week")
	String firstGameOfTheWeek();

	@DefaultMessage("first games of the day")
	String firstGameOfTheDay();

	@DefaultMessage("last games of the year")
	String lastGameOfTheYear();

	@DefaultMessage("last games of the month")
	String lastGameOfTheMonth();

	@DefaultMessage("last games of the week")
	String lastGameOfTheWeek();

	@DefaultMessage("last games of the day")
	String lastGameOfTheDay();

	@DefaultMessage("all games")
	String noop();

	@DefaultMessage("{0}''s opening hands distribution")
	String openingHandCounts(String username);	

	@DefaultMessage("{0}''s hands distribution")
	String handCounts(String username);

	@DefaultMessage("{0}''s graphing colour is")
	String playerColour(String username);

	@DefaultMessage("Top {0} winning streaks")
	@AlternateMessage({
		"=1", "The only winning streak",
		"=0", "The complete lack of winning streaks"
	})
	String winningStreaksSubtitle(@PluralCount int size);	

	@DefaultMessage("Top {0} losing streaks")
	@AlternateMessage({
		"=1", "The only losing streak",
		"=0", "The complete lack of losing streaks"
	})
	String losingStreaksSubtitle(@PluralCount int size);	

  @DefaultMessage("{0,number} games have been played today.")
  @AlternateMessage({
      "=1", "1 game has been played today.",
      "=0", "No games have been played today."})
  String gamesPlayedToday(@PluralCount int gameCount);

	@DefaultMessage("{0}: {1}, {2}: {3}")
	String headToHeadsSummary(String firstPerson, int firstWinCount, String secondPerson, int secondWinCount);

	@DefaultMessage("Win next game {0,number,#.##}%, lose next game {1,number,#.##}%")
	String nextGame(double winNext, double loseNext);

	@DefaultMessage("Do you want to remove the last game that was played at {0,date,HH:mm dd/MM/yyyy} and lost by {1}?")
	String deleteLastGame(Date datePlayed, String loser);

	@DefaultMessage("No-one is currently on a winning streak.")
	String noWinningStreaks();

	@DefaultMessage("{0,list,string} and {1} are all on a winning streak of {2}.")
  @AlternateMessage(
  	{"=0", "{1} is on a winning streak of {2}.",
  	 "=1", "{0,list,string} and {1} are both on a winning streak of {2}." })
	String currentWinningStreak(@PluralCount List<String> losers, String loser, int length);

	@DefaultMessage("No-one is currently on a losing streak.")
	String noLosingStreaks();

	@DefaultMessage("{0,list,string} and {1} are all on a losing streak of {2}.")
  @AlternateMessage(
  	{"=0", "{1} is on a losing streak of {2}.",
  	 "=1", "{0,list,string} and {1} are both on a losing streak of {2}." })
	String currentLosingStreak(@PluralCount List<String> losers, String loser, int length);

	@DefaultMessage("{0}. {1} ({2,number,#.##}%)")
	String leagueRowSummary(int rank, String personName, double score);

	@DefaultMessage("No games have been played.")
	String noLeague();

	@DefaultMessage("''Twas played on {0,date,dd MMM yyyy} at {0,date,HH:mm}.")
	String lastGameDatePlayed(Date datePlayed);

	@DefaultMessage("{0} was daft enough to lose it.")
	String lastGameLoser(String loser);

	@DefaultMessage("No games have been played.")
	String noGamesPlayed();

  @DefaultMessage("{0,list,string} and {1} have all lost {2} today.")
  @AlternateMessage(
  	{"=0", "{1} has lost {2} today.",
  	 "=1", "{0,list,string} and {1} have both lost {2} today."})
	String lossCount(@PluralCount List<String> losers, String loser, String games);

  @DefaultMessage("{0} games")
  @AlternateMessage({"=1", "{0} game" })
  String gameCount(@PluralCount int games);

  @DefaultMessage("No-one is exempt.")
	String nooneExempt();

  @DefaultMessage("{0} is exempt.")
	String exempt(String exemptPlayer);
}
