package ru.itmo.wp.web.page;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;


@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {
    public static final int SIZE = 3;
    private static State state = new State(3);
    public enum Phase {RUNNING, WON_X, WON_O, DRAW};
    public static class State {
        private final int size;
        private final String[][] cells;
        private static Phase phase;
        private boolean crossesMove;

        private State(int size) {
            this.size = size;
            this.cells = new String[size][size];
            phase = Phase.RUNNING;
            this.crossesMove = true;
        }

        public int getSize() {
            return size;
        }

        public String[][] getCells() {
            return cells;
        }

        public Phase getPhase() {
            return phase;
        }

        public boolean isCrossesMove() {
            return crossesMove;
        }
        private static boolean checkDraw() {
            return Arrays.stream(state.cells).flatMap(Arrays::stream).noneMatch(Objects::isNull);
        }

        public static void doMove(int x, int y) {
            if (state.cells[x][y] == null){
                if (x >= state.size || y >= state.size) {
                    return;
                }
                state.cells[x][y] = state.isCrossesMove() ? "X" : "O";
                state.crossesMove ^= true;
            }
        }
    }


    private static boolean checkWin(String c) {
        String[][] cell = state.cells;
        int size = state.size;

        for (int i = 0; i < size; i++) {
            int rowCount = 0;
            int columnCount = 0;
            for (int j = 0; j < SIZE; j++) {
                if (cell[i][j] != null && cell[i][j].equals(c)) {
                    rowCount++;
                }
                if (cell[j][i] != null && cell[j][i].equals(c)) {
                    columnCount++;
                }
            }
            if (rowCount == SIZE || columnCount == SIZE) {
                return true;
            }
        }

        int mainDiagonalCount = 0;
        int antiDiagonalCount = 0;
        for (int i = 0; i < size; i++) {
            if (cell[i][i] != null && cell[i][i].equals(c)) {
                mainDiagonalCount++;
            }
            if (cell[size - 1 - i][i] != null && cell[size - 1 - i][i].equals(c)) {
                antiDiagonalCount++;
            }
        }

        return mainDiagonalCount == SIZE || antiDiagonalCount == SIZE;
    }

    private void onMove(Map<String, Object> view, HttpServletRequest request) {
        view.put("state", state);

        if (state.getPhase() != Phase.RUNNING) {
            return;
        }

        // use stream

//        IntStream.range(0, state.getSize()).forEach(
//                i -> IntStream.range(0, state.getSize()).forEach(
//                        j -> {
//                            if (request.getParameter("cell_" + i + j) != null) {
//                                State.doMove(i, j);
//                            }
//                        }
//                )
//        );
        for (int i = 0; i < state.getSize(); ++i) {
            for (int j = 0; j < state.getSize(); ++j) {
                if (request.getParameter("cell_" + i + j) != null) {
                    State.doMove(i, j);
                }
            }
        }

        // хочу Enum на phase
        if (checkWin("X")) {
            State.phase = Phase.WON_X;
        } else if (checkWin("O")) {
            State.phase = Phase.WON_O;
        } else if (State.checkDraw()){
            State.phase = Phase.DRAW;
        } else {
            State.phase = Phase.RUNNING;
        }
    }

    private void action(Map<String, Object> view, HttpServletRequest request) {
        if (request.getSession().getAttribute("state") == null) {
            request.getSession().setAttribute("state", new State(SIZE));
        }
        view.put("state", request.getSession().getAttribute("state"));
    }


    private void newGame(Map<String, Object> view, HttpServletRequest request) {
        state = new State(SIZE);
        action(view, request);
    }
}