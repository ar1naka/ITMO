#pragma GCC optimize("O3")

#include <string>
#include <vector>
#include <iostream>

using namespace std;

vector<int> colors;
vector<vector<int>> g;

bool dfs(int v) {
    colors[v] = 1;
    for (int u : g[v]) {
        if (colors[u] == 1) {
            return true;
        } else if (!colors[u]) {
            if (dfs(u)) {
                return true;
            }
        }
    }
    colors[v] = 2;
    return false;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n;
    cin >> n;

    g.resize(n + 1);
    for (int i = 1; i < n; i++) {
        string s;
        cin >> s;
        for (int j = 0; j < s.size(); j++) {
            if (s[j] == 'R') {
                g[i + j + 1].push_back(i);
            } else {
                g[i].push_back(i + j + 1);
            }
        }
    }

    colors.resize(n + 1, 0);
    for (int i = 1; i <= n; i++) {
        if (!colors[i]) {
            if (dfs(i)) {
                cout << "NO" << endl;
                return 0;
            }
        }
    }

    cout << "YES" << endl;

    return 0;
}