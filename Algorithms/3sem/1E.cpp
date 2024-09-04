#include <iostream>
#include <vector>
#include <queue>
using namespace std;
#pragma GCC optimize("Ofast")
int n, m;

vector<vector<int>> graph;
vector<int> color;

bool dfs(int v) {
    for (int u : graph[v]) {
        if (color[u] == -1) {
            color[u] = 1 - color[v];
            if (!dfs(u)) return false;
        } else if (color[u] == color[v]) return false;
    }
    return true;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    cin >> n >> m;

    graph.resize(n);
    color.resize(n, -1);
    for (int i = 0; i < m; i++) {
        int a, b;
        cin >> a >> b;
        graph[a - 1].push_back(b - 1);
        graph[b - 1].push_back(a - 1);
    }

    for (int i = 0; i < n; i++) {
        if (color[i] == -1) {
            if (!dfs(i)) {
                cout << "-1";
                break;
            }
            cout << color[i] + 2 << " ";
        } else {cout << color[i] << " ";
        }
    }
    return 0;
}