#include <iostream>
#include <vector>

using namespace std;
vector<vector<int>> A;
vector<bool> used;

int dfs(int u, int t, int c) {
    if (u == t) {
        return c;
    }
    used[u] = true;

    for (int i = 0; i < A.size(); i++) {
        if (!used[i] && A[u][i] > 0){
            int delta = dfs(i, t, min(c, A[u][i]));
            A[i][u] += delta;
            A[u][i] -= delta;
            if (delta > 0) {
                return delta;
            }
        }
    }
    return 0;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n, m;
    cin >> n >> m;
    A.resize(n);
    for (int i = 0; i < n; i++) {
        A[i].resize(n);
    }
    used.resize(n);

    for (int i = 0; i < m; i++){
        int a, b, c;
        cin >> a >> b >> c;
        A[a - 1][b - 1] = c;
    }
    int anser = 0;
    int delta;
    while (true) {
        used.assign(n, false);
        delta = dfs(0, n - 1, 10000000);
        anser += delta;
        if (delta <= 0) {
            break;
        }
    }
    cout << anser;
}