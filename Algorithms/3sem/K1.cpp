#include <iostream>
#include <vector>

using namespace std;

int main() {
    int n, m;
    cin >> n >> m;

    vector<vector<int>> graph(n + 1);
    vector<int> order(n + 1);
    vector<int> p(n + 1, -1);

    for (int i = 0; i < m; i++) {
        int a, b;
        cin >> a >> b;
        graph[a].push_back(b);
    }

    for (int i = 1; i <= n; i++) {
        cin >> order[i];
        p[order[i]] = i;
    }

    bool c = false;
    for (int u = 1; u <= n; u++) {
        for (int v : graph[u]) {
            if (p[u] > p[v]) {
                c = true;
                break;
            }
        }
    }
    if (c) {
        cout << "NO" << endl;
    } else {
        cout << "YES" << endl;
    }
    return 0;
}