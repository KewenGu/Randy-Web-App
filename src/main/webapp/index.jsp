<html>
    <style>
      div {
        text-align: center;
        font-family: 'Century Gothic', CenturyGothic, AppleGothic, sans-serif;
        font-size: 200%;
        line-height: 2.5;
        margin: 7% 7%;
        padding: 2em;
        background-color: ivory;
        border: 5px inset #999
      }
      .button {
        width: 240px;
        height: 40px;
        font-size: large;
      }
      .submit {
        padding: 10px 20px;
        margin-top: 50px;
        font-size: large;
        font-weight: bold;
        background-color: inherit;
        border: 2px solid #000;
      }

    </style>
    <body>
      <div>
        <h1>Randy Path Planning</h1>
        <form action="/sms" method="get">
          <input type="text" name="start" class="button" placeholder="Enter Start Location: " required>
          <br>
          <input type="text" name="dest" class="button" placeholder="Enter Destination: " required>
          <br>
          <input type="submit" class="submit" value="GO!">
        </form>
      </div>
    </body>
</html>
