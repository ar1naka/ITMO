#include <iostream>
#include <vector>

using namespace std;

vector<vector<int>> A;
vector<int> B;
vector<vector<bool>> dom;


vector<bool> used;
bool dfs(int v) {
    if (used[v]) return false;
    used[v] = true;
    for (int u : A[v]) {
        if (B[u] == -1 || dfs(B[u])) {
            B[u] = v;
            return true;
        }
    }
    return false;
}
int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    int n, m, a, b;

    cin >> n >> m >> a >> b;
    dom.resize(n, vector<bool>(m, false));

    int empty = 0;
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            char x;
            cin >> x;
            dom[i][j] = (x == '*');
            empty += (x == '*');
        }
    }


    if (2 * b <= a) {
        cout << b * empty;
        return 0;
    }


    A.resize(n * m);
    B.assign(n * m, -1);
    used.resize(n * m);

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            if (dom[i][j] && (i + j) % 2 == 1) {
                if (j > 0 && dom[i][j - 1]) {
                    A[i * m + j].push_back(i * m + j - 1);
                }
                if (j < m - 1 && dom[i][j + 1]) {
                    A[i * m + j].push_back(i * m + j + 1);
                }
                if (i > 0 && dom[i - 1][j]) {
                    A[i * m + j].push_back((i - 1) * m + j);
                }
                if (i < n - 1 && dom[i + 1][j]) {
                    A[i * m + j].push_back((i + 1) * m + j);
                }
            }
        }
    }


    for (int i = 0; i < n * m; i++) {
        if (dfs(i)) {
            for (int j = 0; j < used.size(); j++) {
                used[j] = false;
            }

        }
    }

    int answer = 0;
    for (int j : B) {
        answer += (j != -1);
    }

    cout << answer * a + (empty - 2 * answer) * b;

    return 0;

}