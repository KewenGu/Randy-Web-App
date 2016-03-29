package twilio;

import AStar.Node;
import DataAccess.DirectionBuilder;
import DataAccess.Map;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.List;
import java.lang.String;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.Message;

import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.standard.StandardAnalyzer;



public class TwilioServlet extends HttpServlet {

    // service() responds to both GET and POST requests.
    // You can also use doGet() or doPost()
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String msgBody = request.getParameter("Body");
        String startLoc = request.getParameter("start");
        String destLoc = request.getParameter("dest");
        String resp = "";
        String[] locations;
        String start;
        String dest;
        List<Node> path;
        List<String> directions;
        int i, j, k;

        ServletContext context = getServletContext();
        String mapPath = context.getRealPath("/WEB-INF/classes/res/SuperMap");
        Map map = Map.loadMap(mapPath, "Campus");
        Node startNode;
        Node destNode;

        String dirPath = context.getRealPath("/WEB-INF/classes/res/dict/");
        Directory directory = FSDirectory.open(Paths.get(dirPath));
        final SpellChecker spellChecker = new SpellChecker(directory);
        String filePath = context.getRealPath("/WEB-INF/classes/res/dict/allRooms.txt");
        spellChecker.indexDictionary(new PlainTextDictionary(Paths.get(filePath)), new IndexWriterConfig(new StandardAnalyzer()), false);
        final int suggestionNumber = 5;
        String[] suggestions1;
        String[] suggestions2;

        if (msgBody!=null && !msgBody.isEmpty()) {

          locations = msgBody.split("@");

          if (locations.length == 3) {
            start = locations[1];
      	    dest = locations[2];
            i = 0;
            j = 0;
            k = 0;
            resp = "";
            startNode = map.findNodeByName(start);
            destNode = map.findNodeByName(dest);

            if (startNode != null && destNode != null) {
              path = Node.getPathFromNode(startNode, destNode, map);
              directions = DirectionBuilder.getDirectionsList(path, 1, 1);

              resp = "Directions from " + start + " to " + dest + ": \r\n\r\n";
              for (String s: directions) {
                i++;
                resp += "\r\n" + Integer.toString(i) + ". " + s + "\r\n";
              }
              resp += "\r\n\r\nRegards, \r\nTeam Schadeufreude";

            }
            else if (startNode == null && destNode != null) {
              suggestions1 = spellChecker.suggestSimilar(start, suggestionNumber);
              resp = "By " + start + ", did you mean: \r\n";
              for (String word : suggestions1) {
                j++;
              	resp += Integer.toString(j) + ". " + word + "\r\n";
              }
            }
            else if (startNode != null && destNode == null) {
              suggestions2 = spellChecker.suggestSimilar(dest, suggestionNumber);
              resp += "By " + dest + ", did you mean: \r\n";
              for (String word : suggestions2) {
                k++;
              	resp += Integer.toString(k) + ". " + word + "\r\n";
              }
            }
            else if (startNode == null && destNode == null) {
              suggestions1 = spellChecker.suggestSimilar(start, suggestionNumber);
              resp += "By " + start + ", did you mean: \r\n";
              for (String word : suggestions1) {
                j++;
              	resp += Integer.toString(j) + ". " + word + "\r\n";
              }
              resp += "\r\n\r\n";
              suggestions2 = spellChecker.suggestSimilar(dest, suggestionNumber);
              resp += "By " + dest + ", did you mean: \r\n";
              for (String word : suggestions2) {
                k++;
              	resp += Integer.toString(k) + ". " + word + "\r\n";
              }
            }
          }
          else {
            resp = "Your request cannot be completed! \r\nCorrect input method: @Start Location@Destination.";
          }


          TwiMLResponse twiml = new TwiMLResponse();
          Message message = new Message(resp);

          try {
              twiml.append(message);
          } catch (TwiMLException e) {
              e.printStackTrace();
          }

          response.setContentType("application/xml");
          response.getWriter().print(twiml.toXML());
        }


        if (startLoc!=null && !startLoc.isEmpty() && destLoc!=null && !destLoc.isEmpty()) {

          startNode = map.findNodeByName(startLoc);
          destNode = map.findNodeByName(destLoc);

          response.setContentType("text/html; charset=UTF-8");
          PrintWriter pw = response.getWriter();

          if (startNode != null && destNode != null) {
            path = Node.getPathFromNode(startNode, destNode, map);
            directions = DirectionBuilder.getDirectionsList(path, 1, 1);


            printDirections(pw, startLoc, destLoc, directions);
          }
          else if (startNode == null && destNode != null) {
            suggestions1 = spellChecker.suggestSimilar(startLoc, suggestionNumber);
            printSpellChecking(pw, startLoc, suggestions1);
          }
          else if (startNode != null && destNode == null) {
            suggestions2 = spellChecker.suggestSimilar(destLoc, suggestionNumber);
            printSpellChecking(pw, destLoc, suggestions2);
          }
          else if (startNode == null && destNode == null) {
            suggestions1 = spellChecker.suggestSimilar(startLoc, suggestionNumber);
            suggestions2 = spellChecker.suggestSimilar(destLoc, suggestionNumber);
            printSpellChecking(pw, startLoc, destLoc, suggestions1, suggestions2);
          }
        }
    }

    private static void printDirections(PrintWriter pw, String startLoc, String destLoc, List<String> directions) {
      pw.println("<html>");
      pw.println("<head>");
      pw.println("<title>Your Directions - Randy Path Planning</title>");
      setHTMLStyle(pw);
      pw.println("</head>");
      pw.println("<body>");
      pw.println("<div>");
      pw.println("<h3>Directions from " + startLoc + " to " + destLoc + ": </h3><br>");
      pw.println("<ol>");
      for (String s: directions) {
        pw.println("<li>" + s + "</li>");
      }
      pw.println("</ol>");
      pw.println("<br><br><p>Thank you for using Randy!<br>Team Schadeufreude</p>");
      pw.println("</div>");
      pw.println("</body>");
      pw.println("</html>");
    }

    private static void printSpellChecking(PrintWriter pw, String word, String[] suggestions) {
      pw.println("<html>");
      pw.println("<head>");
      pw.println("<title>Your Directions - Randy Path Planning</title>");
      setHTMLStyle(pw);
      pw.println("</head>");
      pw.println("<body>");
      pw.println("<div>");
      pw.println("<p>By " + word +", did you mean:</p>");
      pw.println("<ol>");
      for (String s: suggestions) {
        pw.println("<li>" + s + "</li>");
      }
      pw.println("</ol>");
      pw.println("<br>");
      pw.println("<p>Please go back and try again.<br>Team Schadeufreude</p>");
      pw.println("</div>");
      pw.println("</body>");
      pw.println("</html>");
    }


    private static void printSpellChecking(PrintWriter pw, String word1, String word2, String[] suggestions1, String[] suggestions2) {
      pw.println("<html>");
      pw.println("<head>");
      pw.println("<title>Your Directions - Randy Path Planning</title>");
      setHTMLStyle(pw);
      pw.println("</head>");
      pw.println("<body>");
      pw.println("<div>");
      pw.println("<p>By " + word1 +", did you mean:</p>");
      pw.println("<ol>");
      for (String s1: suggestions1) {
        pw.println("<li>" + s1 + "</li>");
      }
      pw.println("</ol>");
      pw.println("<br>");
      pw.println("<p>By " + word2 +", did you mean:</p>");
      pw.println("<ol>");
      for (String s2: suggestions2) {
        pw.println("<li>" + s2 + "</li>");
      }
      pw.println("</ol>");
      pw.println("<br>");
      pw.println("<p>Please go back and try again.<br>Team Schadeufreude</p>");
      pw.println("</div>");
      pw.println("</body>");
      pw.println("</html>");
    }

    private static void setHTMLStyle(PrintWriter pw) {
      pw.println("<style>");
      pw.println("div {" +
        "font-family: 'Century Gothic', CenturyGothic, AppleGothic, sans-serif;" +
        "font-size: 200%;" +
        "line-height: 2.5;" +
        //"position: absolute;" +
        //"top: 50%;" +
        //"left: 50%;" +
        //"transform: translate3d(-50%,-50%, 0);" +
        "margin: 7% 7%;" +
        "padding: 2em;" +
        "background-color: ivory;" +
      	"border-radius: 10px;" +
        "border-right: 1px solid #999;" +
      	"border-bottom: 1px solid #999;}");
      pw.println("</style>");
    }

/*
    private static void printErrorMsg(PrintWriter pw) {
      pw.println("<html>");
      pw.println("<head>");
      pw.println("<title>Your Directions - Randy Path Planning</title>");
      pw.println("<link rel='stylesheet' href='css/main.css'>");
      pw.println("</head>");
      pw.println("<body>");
      pw.println("<div id='direction-block'>");
      pw.println("<p>Sorry, we cannot find the directions for you, please provide valid location names.</p>");
      pw.println("<p>Team Schadeufreude</p>");
      pw.println("</div>");
      pw.println("</body>");
      pw.println("</html>");
    }
*/
}
