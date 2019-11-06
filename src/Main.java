public class Main {
        public static void main (String[] args) {
           FD fd = new FD("172.17.0.3",
                    "postgres",
                    "xxxxxxxxxxx",
                    "5432",
                    "homework3");
           fd.initialize();
        }
}
