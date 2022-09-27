# 냄새2. 중복 코드

## 리팩토링 기술 4: 함수 추출하기

## 리팩토링 기술 5: 코드 정리하기
- 변수를 한꺼번에 위에 선언하지말고 사용하는 함수 바로 위에 선언하자
- before
    ```
    private void printReviewers() throws IOException {
        // Get github issue to check homework
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);
        Set<String> reviewers = new HashSet<>();


        issue.getComments().forEach(c -> reviewers.add(c.getUserName()));
        reviewers.forEach(System.out::println);
    }
    ```
- after
    ```
    private void printReviewers() throws IOException {
        // Get github issue to check homework
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);

    
        Set<String> reviewers = new HashSet<>(); // 사용하고자 하는 함수 바로 위에 선언
        issue.getComments().forEach(c -> reviewers.add(c.getUserName()));

        
        reviewers.forEach(System.out::println);
    }

    ```

## 리팩토링 기술 6: 메소드 올리기

# 냄새 3. 긴함수

## 리팩토링 7. 임시 변수를 질의 함수로 바꾸기
- before
    ```
    ...
    participants.forEach(p -> {
                long count = p.homework().values().stream()
                        .filter(v -> v == true)
                        .count();
                double rate = count * 100 / totalNumberOfEvents;

                String markdownForHomework = String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, totalNumberOfEvents), rate);
                writer.print(markdownForHomework);
            });
    }
    ```
  rate 변수를 함수로 바꾸어서 매개변수에 삽입

- after
    ```
    ...
    /** 본문함수 **/
    participants.forEach(p -> {
                String markdownForHomework = getMarkdownForParticipant(totalNumberOfEvents, p);
                writer.print(markdownForHomework);
            });
    }
    
    private double getRate(int totalNumberOfEvents, Participant p) {
        long count = p.homework().values().stream()
                .filter(v -> v == true)
                .count();
        double rate = count * 100 / totalNumberOfEvents;
        return rate;
    }

    private String getMarkdownForParticipant(int totalNumberOfEvents, Participant p) {
        return String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, totalNumberOfEvents), getRate(totalNumberOfEvents, p)); 
        /** rate -> getRate() **/
    }
    ```



## 리팩토링 8. 매개변수 객체 만들기(Introduce Parameter Object)
> 하나의 함수를 여러개의 함수로 분리하면서 해당 함수로 전달해야 할 매개변수가 많아질때1.

- before
    ```
    private double getRate(int totalNumberOfEvents, Participant p) {
            ...
            return rate;
    }
    ```
- after
    ```
    private double getRate(ParticipantPrinter p) {
                ...
                return rate;
    }
    ```


## 리팩토링 9. 객체 통째로 넘기기(Preserve Whole Object)
> 하나의 함수를 여러개의 함수로 분리하면서 해당 함수로 전달해야 할 매개변수가 많아질때2.

- before
```java
public void main(){
    List<Participant> participants = new CopyOnWriteArrayList<>();
    ...
    ...
    participants.forEach(p -> {
                String markdownForHomework = getMarkdownForParticipant(p.username(), p.homework());
                writer.print(markdownForHomework);
    });
}

private String getMarkdownForParticipant(String username, Map<Integer, Boolean> homework) {
        return String.format("| %s %s | %.2f%% |\n", username,
                checkMark(homework, this.totalNumberOfEvents),
                getRate(homework));
}
```

- after
```java
public void main(){
    List<Participant> participants = new CopyOnWriteArrayList<>();
    ...
    ...
    participants.forEach(p -> {
                String markdownForHomework = getMarkdownForParticipant(p);
                writer.print(markdownForHomework);
    });
}

private String getMarkdownForParticipant(Participant participant) {
        return String.format("| %s %s | %.2f%% |\n", username,
                checkMark(homework, this.totalNumberOfEvents),
                getRate(homework));
}
```
- 의존성을 고민할 것(다른 곳에서 안쓰이는가?) 여기선 `getMarkdownForParticipant()`

## 리팩토링 10. 함수를 명령으로 바꾸기
> 하나의 함수를 여러개의 함수로 분리하면서 해당 함수로 전달해야 할 매개변수가 많아질때3.

### before
** StudyDashBoard.class **
```java
    private void print() throws IOException, InterruptedException {
        
        ...

        /** 이 부분뿐만 아니라 함께 관련된 함수를 명령형으로 바꾸어보자 **/
        try (FileWriter fileWriter = new FileWriter("participants.md");
            PrintWriter writer = new PrintWriter(fileWriter)) {
            participants.sort(Comparator.comparing(Participant::username));

            writer.print(header(participants.size()));

            participants.forEach(p -> {
                String markdownForHomework = getMarkdownForParticipant(p);
                writer.print(markdownForHomework);
            });
        }
    }

    /** 관련된 함수들 **/
    private String getMarkdownForParticipant(Participant p) {
        return String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, this.totalNumberOfEvents),
                p.getRate(this.totalNumberOfEvents));
    }

    /**
     * | 참여자 (420) | 1주차 | 2주차 | 3주차 | 참석율 |
     * | --- | --- | --- | --- | --- |
     */
    private String header(int totalNumberOfParticipants) {
        StringBuilder header = new StringBuilder(String.format("| 참여자 (%d) |", totalNumberOfParticipants));

        for (int index = 1; index <= this.totalNumberOfEvents; index++) {
            header.append(String.format(" %d주차 |", index));
        }
        header.append(" 참석율 |\n");

        header.append("| --- ".repeat(Math.max(0, this.totalNumberOfEvents + 2)));
        header.append("|\n");

        return header.toString();
    }

    /**
     * |:white_check_mark:|:white_check_mark:|:white_check_mark:|:x:|
     */
    private String checkMark(Participant p, int totalEvents) {
        StringBuilder line = new StringBuilder();
        for (int i = 1 ; i <= totalEvents ; i++) {
            if(p.homework().containsKey(i) && p.homework().get(i)) {
                line.append("|:white_check_mark:");
            } else {
                line.append("|:x:");
            }
        }
        return line.toString();
    }

```

### After

** StudyDashBoard.class **
```java
    private void print() throws IOException, InterruptedException {
        
        ...

        /** 이 부분뿐만 아니라 함께 관련된 함수를 명령형으로 바꾸어보자 **/
        /*
        try (FileWriter fileWriter = new FileWriter("participants.md");
            PrintWriter writer = new PrintWriter(fileWriter)) {
            participants.sort(Comparator.comparing(Participant::username));

            writer.print(header(participants.size()));

            participants.forEach(p -> {
                String markdownForHomework = getMarkdownForParticipant(p);
                writer.print(markdownForHomework);
            });
        }
        */
        new StudyPrinter(this.totalNumberOfEvents, participants).execute();
    }
    
    

```

**StudyPrinter.class**
- 각 함수에 필요한 공통 매개변수들을 필드로 빼내어주어(Introduce Field) 함수들의 매개변수를 줄여준다
- 이때의 공통 매개변수는 `StudyDashBoard.class`에서 사용하던 변수이어야한다(그래야 생성자 주입으로 필드 삽입 가능)
```java
public class StudyPrinter {

    /** 각 함수들의 공통 매개변수를 필드로 빼내어준다**/
    /** 이때의 공통 매개변수는 StudyDashBoard에서 사용하던 변수이어야한다(그래야 생성자 주입으로 필드 삽입 가능)**/
    private int totalNumberOfEvents;
    private List<Participant> participants;

    public StudyPrinter(int totalNumberOfEvents, List<Participant> participants) {
        this.totalNumberOfEvents = totalNumberOfEvents;
        this.participants = participants;
    }

    public void execute() throws IOException {
        try (FileWriter fileWriter = new FileWriter("participants.md");
             PrintWriter writer = new PrintWriter(fileWriter)) {
            this.participants.sort(Comparator.comparing(Participant::username));

            writer.print(header(this.participants.size()));

            this.participants.forEach(p -> {
                String markdownForHomework = getMarkdownForParticipant(p);
                writer.print(markdownForHomework);
            });
        }
    }

    private String getMarkdownForParticipant(Participant p) {
        return String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, this.totalNumberOfEvents),
                p.getRate(this.totalNumberOfEvents));
    }

    /**
     * | 참여자 (420) | 1주차 | 2주차 | 3주차 | 참석율 |
     * | --- | --- | --- | --- | --- |
     */
    private String header(int totalNumberOfParticipants) {
        StringBuilder header = new StringBuilder(String.format("| 참여자 (%d) |", totalNumberOfParticipants));

        for (int index = 1; index <= this.totalNumberOfEvents; index++) {
            header.append(String.format(" %d주차 |", index));
        }
        header.append(" 참석율 |\n");

        header.append("| --- ".repeat(Math.max(0, this.totalNumberOfEvents + 2)));
        header.append("|\n");

        return header.toString();
    }

    /**
     * |:white_check_mark:|:white_check_mark:|:white_check_mark:|:x:|
     */
    private String checkMark(Participant p, int totalEvents) {
        StringBuilder line = new StringBuilder();
        for (int i = 1 ; i <= totalEvents ; i++) {
            if(p.homework().containsKey(i) && p.homework().get(i)) {
                line.append("|:white_check_mark:");
            } else {
                line.append("|:x:");
            }
        }
        return line.toString();
    }
}
```

### 응용버전
- 위 함수를 이용하여 printe.mode를 정할 수 있다

**StudyDashBoard.class**

```java
new StudyPrinter(this.totalNumberOfEvents, this.participants, PrinterMode.MARKDOWN).execute();
```

**StudyPrinter.class**
```java
public class StudyPrinter {

    private int totalNumberOfEvents;
    private List<Participant> participants;
    private PrinterMode printerMode; //추가

    public StudyPrinter(int totalNumberOfEvents, List<Participant> participants, PrinterMode printerMode) {
        this.totalNumberOfEvents = totalNumberOfEvents;
        this.participants = participants;
        this.participants.sort(Comparator.comparing(Participant::username));
        this.printerMode = printerMode;
    }

    public void execute() throws IOException {
        switch (printerMode) {
            case CVS -> {
                ...
            }
            case CONSOLE -> {
                ...
            }
            case MARKDOWN -> {
                ...
            }
        }
    }
    
    // 모든 case에 공통적으로 필요한 함수들
    ...
    
    // case: Mardown에 필요한 함수들
    ...
    
    // case: Console에 필요한 함수들
    ...
    
    // case: CVS 필요한 함수들
    ...
    
}
```

## 리팩토링 11. 조건문 분해하기
> 조건문 분리

### before
```java
private Participant findParticipant(String username, List<Participant> participants) {
        Participant participant;
        if (participants.stream().noneMatch(p -> p.username().equals(username))) {
            participant = new Participant(username);
            participants.add(participant);
        } else {
            participant = participants.stream().filter(p -> p.username().equals(username)).findFirst().orElseThrow();
        }

        return participant;
    }
```

### after 1단계
```java
    private Participant findParticipant(String username, List<Participant> participants) {
        Participant participant = null;
        if (isNewParticipant(username, participants)) {
            participant = createNewParticipant(username, participants);
        } else {
            participant = findExistingParticipant(username, participants);
        }
        return participant;
    }
    
    private Participant findExistingParticipant(String username, List<Participant> participants) {
        Participant participant;
        participant = participants.stream().filter(p -> p.username().equals(username)).findFirst().orElseThrow();
        return participant;
    }

    private Participant createNewParticipant(String username, List<Participant> participants) {
        Participant participant;
        participant = new Participant(username);
        participants.add(participant);
        return participant;
    }

    private boolean isNewParticipant(String username, List<Participant> participants) {
        return participants.stream().noneMatch(p -> p.username().equals(username));
    }

```

### after 2단계
```java

    private Participant findParticipant(String username, List<Participant> participants) {
        return isNewParticipant(username, participants) ?
                createNewParticipant(username, participants) :
                findExistingParticipant(username, participants);
    }

    private Participant findExistingParticipant(String username, List<Participant> participants) {
        Participant participant;
        participant = participants.stream().filter(p -> p.username().equals(username)).findFirst().orElseThrow();
        return participant;
    }

    private Participant createNewParticipant(String username, List<Participant> participants) {
        Participant participant;
        participant = new Participant(username);
        participants.add(participant);
        return participant;
    }

    private boolean isNewParticipant(String username, List<Participant> participants) {
        return participants.stream().noneMatch(p -> p.username().equals(username));
    }

```

## 리팩토링 12. 반복문 쪼개기(Split Loop)
> 같은 조건으로 여러개의 Switch 문이 있을 때

### before
```
    private void print() throws IOException, InterruptedException {
        
        ...
        public void run() {
                    try {
                        GHIssue issue = ghRepository.getIssue(eventId);
                        List<GHIssueComment> comments = issue.getComments();
                        Date firstCreatedAt = null;
                        Participant first = null;

                        for (GHIssueComment comment : comments) {
                            Participant participant = findParticipant(comment.getUserName(), participants);
                            participant.setHomeworkDone(eventId);

                            if (firstCreatedAt == null || comment.getCreatedAt().before(firstCreatedAt)) {
                                firstCreatedAt = comment.getCreatedAt();
                                first = participant;
                            }
                        }

                        firstParticipantsForEachEvent[eventId - 1] = first;
                        latch.countDown();
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
        ...
    }
```

### After 1단계
- 하나의 for문을 두개로 나눈다
```
  
    private void print() throws IOException, InterruptedException {
        
        ...
        public void run() {
                    try {
                        GHIssue issue = ghRepository.getIssue(eventId);
                        List<GHIssueComment> comments = issue.getComments();
                       
                       /** 1번째 for**/
                       for (GHIssueComment comment : comments) {
                            Participant participant = findParticipant(comment.getUserName(), participants);
                            participant.setHomeworkDone(eventId);
                        }


                        /** 2번째 for **/
                        Date firstCreatedAt = null;
                        Participant first = null;
                        for (GHIssueComment comment : comments) {
                            Participant participant = findParticipant(comment.getUserName(), participants); // participant는 필요하기 때문에 추가해준다.
                            
                            if (firstCreatedAt == null || comment.getCreatedAt().before(firstCreatedAt)) {
                                firstCreatedAt = comment.getCreatedAt();
                                first = participant;
                            }
                        }

                        firstParticipantsForEachEvent[eventId - 1] = first;
                        latch.countDown();
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
            }
        ...
    }
```

### After 2단계
- 나눈 함수들을 함수화한다
```
  
    private void print() throws IOException, InterruptedException {
        
        ...
        public void run() {
                    try {
                        GHIssue issue = ghRepository.getIssue(eventId);
                        List<GHIssueComment> comments = issue.getComments();
                       
                       GHIssue issue = ghRepository.getIssue(eventId);
                        List<GHIssueComment> comments = issue.getComments();
                        
                        checkHomework(comments, eventId); //첫번째 for
                        firstParticipantsForEachEvent[eventId - 1] = findFirst(comments); //두번째 for + Inline
                        
                        latch.countDown();
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
        }
        ...
    }
    private Participant findFirst(List<GHIssueComment> comments) throws IOException {
        Date firstCreatedAt = null;
        Participant first = null;
        for (GHIssueComment comment : comments) {
            Participant participant = findParticipant(comment.getUserName(), participants);

            if (firstCreatedAt == null || comment.getCreatedAt().before(firstCreatedAt)) {
                firstCreatedAt = comment.getCreatedAt();
                first = participant;
            }
        }
        return first;
    }

    private void checkHomework(List<GHIssueComment> comments, int eventId) {
        for (GHIssueComment comment : comments) {
            Participant participant = findParticipant(comment.getUserName(), participants);
            participant.setHomeworkDone(eventId);
        }
    }
```

## 리팩토링 13. 조건문을 다형성으로 바꾸기(Replace Conditional with Polymorphism)
> 반복문 안에서 여러 작업을 하고 있어서 하나의 메소드로 추출하기 어려울 때

- if(true) else 와 다르게 여러 타입에 따라 각기 다른 로직으로 처리해야하는 경우에 다형성을 적용해서 조건문을 보다 명확하게 분리할 수 있다
    - ex) `if(a == 'book') return A elif(a == 'movie') return B else return C`
- 공통으로 사용되는 로직은 상위클래스에 두고 달라지는 부분만 하위 클래스에 둠으로써, 달라지는 부분만 강조할 수 있다

기존의 [위에서 본 StudyPrinter클래스]()를 참고하여 변경해보자
- 상속받아서 다형성을 구현할 것이기때문에 `Printer.mode`는 지운다

**`StudyPrinter.class`**
```java
public class StudyPrinter {

    protected int totalNumberOfEvents; //private -> protected (자식함수가 사용하기 위해)
    protected List<Participant> participants; //private -> protected (자식함수가 사용하기 위해)
    private PrinterMode printerMode; //삭제
    
    public void execute() throws IOException {
        switch (printerMode) {
            case CVS -> {
                FucntionAForCvsLogic..
                FucntionBForCvsLogic..
                CommonFunctionA
            }
            case CONSOLE -> {
                FucntionAForConsoleLogic..
                FucntionBForConsoleLogic..
                CommonFunctionA
            }
            case MARKDOWN -> {
                FucntionAForMarkdownLogic..
                FucntionBForMarkdownLogic..
                CommonFunctionA
            }
        }
    }
    
    // 모든 case에 공통적으로 필요한 함수들
    protected Reuslt CommonFunctionA(..){ //private -> protected (자식함수가 사용하기 위해)
        ...
    }
    
    // case: Mardown에 필요한 함수들
    ...
    
    // case: Console에 필요한 함수들
    ...
    
    // case: CVS 필요한 함수들
    private FucntionAForCvsLogic(){
    ..
    }
    private FucntionBForCvsLogic(){
    ..
    }


```

### `StudyPrinter`를 상속받는 함수
**CVS.class**
```java
public class CvsPrinter extends StudyPrinter{
    
    public CvsPrinter(int totalNumberOfEvents, List<Participant> participants){ //PrinterMode 지우기
        super(totalNumberOfEvents, participants);
    }
    
    @Override
    public void execute() throws IOException{
        //super.execute();
        FucntionAForCvsLogic..
        FucntionBForCvsLogic..
        CommonFunctionA //부모 클래스에 있는 함수 사용
    }
    
    // execute() - case: CVS 로직에 필요한 함수들
    private FucntionAForCvsLogic(){
    ..
    }
    private FucntionBForCvsLogic(){
    ..
    }
    
    //execute() - 공통 로직에 필요한 함수들은 부모 클래스에 있기때문에 없어야한다
    /*
    protected Reuslt CommonFunctionA(..){ //private -> protected (자식함수가 사용하기 위해)
        ...
    }
    */
}
```


