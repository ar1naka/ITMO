#include <iostream>
#include <vector>

using namespace std;

vector<vector<int>> A;
vector<bool> used;
vector<int> B;

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
    int n, m;
    cin >> n >> m;
    A.resize(n + 1);
    used.resize(n + 1);
    B.resize(n + 1, -1);
    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;
        A[u].push_back(v);
    }
    int cnt = 0;
    for (int i = 1; i <= n; i++) {
        used.assign(n + 1, false);
        if (dfs(i)) {
            cnt++;
        }
    }
    cout << n - cnt << endl;
}