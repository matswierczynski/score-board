# Score Board

## Initial assumptions
<ol>
<li>To start a game, two non null and not empty Teams are required. Breaking any of those tho conditions will throw an error</li>
<li>If the game between two teams in currently in progress, an attempt to initialize a game between the same teams will throw an exception</li>
<li>The Scoreboard's state exists as long as its instance is present - there is no layer of persistence. Beyond that, there is no property that stored the history of the whole cup
which means there is no validation related to the whole history of the cup.
If a game game between two teams has already finished, no exception will be thrown if a game between two teams, which have already played against each other, is initialized again.
<li>An update of a score is possible only for teams which actively compete in a cup. Trying to update a score of a game which is unknown for the Scoreboard will throw an exception.</li>
<li><i>Get summary</i> returns an ordered list of ongoing games, sorted by <code>total score (desc) </code> and then by <code>start date (desc)</code>.
By a start date we mean an exact time when the game has been started - updating a game's score won't update this field and there is no <code>lastModifiedDate</code> field
which potentially could store such information.</li>
<li><i>Get summary</i> returns a list of <b>Game</b> instances. To print the output in more user-friendly format a <i>toString</i> method has been overridden to present the same result 
but with a predefined style</li>
<li>Game class is not fully unmodifiable - it possible to call <i>updateScore</i> method on any instance of this class what will internally modify its state.
It is a handy feature, especially from the Scoreboard's point of view - updating a given game's internals will not only modify the game itself buy also 
the scoreboard's view of this game. We could make the Game class fully immutable and instead of changing its internal state - providing its modified copy but 
it will require us to update both game and Scoreboard's copy of the game to propagate the changes into all components.</li>
<li>Scoreboard's internal state is represented by a <i>HashSet</i> which <b>is not thread safe</b>. Apart from that, Scoreboard initializes the chosen <i>Set</i>
implementation in a constructor. Instead of this, if we would like to get a client a full control over the persistent layer, we could expose it as an interface (port)
and then require a client-suited implementation of it (adapter).</li>
</ol>

## How to run
The provided implementation has a form of a simple library and is not intended to be a stand-alone project. We could simply use as a part of a Service by providing
the required dependencies (mainly the Clock instance). However, it is possible to build a Maven's artifact containing the library by executing:<br>
<code> mvn clean install</code><br>
It will also verify if the provided tests still pass.