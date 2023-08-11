class Message {
    String type;
    String sender;
    String receiver;
    String content;

    Message(String type, String sender, String receiver, String content) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    Message(String message) {
        try {
            String[] options = message.split("$");
            for (String arg : options) {
                System.out.println(arg);
            }
            if (options.length != 4) {
                throw new Exception("Message has invalid format!");
            }
            this.type = options[0];
            this.sender = options[1];
            this.receiver = options[2];
            this.receiver = options[3];
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String toString() {
        return String.format("%s$%s$%s$%s", type, sender, receiver, content);
    }
}
