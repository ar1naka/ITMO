#pragma GCC optimize("O3")

#include <vector>
#include <climits>
#include <iostream>

using namespace std;

struct edge {
    int a;
    int b;
    int w;
};

vector<int> p;
vector<edge> g;

int ford_bellman(int n) {
    int cycle;
    vector<long long> d(n);
    p.resize(n, -1);
    for (size_t i = 0; i < n; i++) {
        cycle = -1;
        for (auto & e : g) {
            if (d[e.a] + e.w < d[e.b]) {
                d[e.b] = max(d[e.a] + e.w, LONG_LONG_MIN);
                p[e.b] = e.a;
                cycle = e.b;
            }
        }
    }
    return cycle;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n;
    cin >> n;

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            int w;
            cin >> w;
            if (w < 100000) {
                g.push_back({i, j, w});
            }
        }
    }

    int v = ford_bellman(n);
    if (v == -1) {
        cout << "NO" << endl;
    } else {
        cout << "YES" << endl;
        int u = v;
        for (size_t i = 0; i < n; i++) {
            u = p[u];
        }
        vector<int> path;
        for (int cur = u; ; cur = p[cur]) {
            path.push_back(cur);
            if (cur == u && 1 < path.size()) {
                break;
            }
        }
        cout << path.size() - 1 << endl;
        for (size_t i = path.size() - 1; i-- > 0; ) {
            cout << path[i] + 1 << " ";
        }
    }

    return 0;
}