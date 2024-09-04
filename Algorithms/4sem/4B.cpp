#pragma GCC optimize("O3")

#include <iostream>
#include <vector>
#include <queue>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n;
    cin >> n;
    vector<int> ln(n + 1, 0);
    vector<int> col(n + 1, 0);
    vector<int> p(n + 1, -1);
    vector<int> way(n + 1, -1);
    vector<vector<int>> a(n + 1, vector<int>(n + 1));

    for (int i = 1; i <= n; ++i) {
        for (int j = 1; j <= n; ++j) {
            cin >> a[i][j];
        }
    }

    for (int i = 1; i <= n; ++i) {
        vector<int> minv(n + 1, INT_MAX);
        vector<bool> used(n + 1);
        int cur, delta, v, j0 = 0;
        p[0] = i;
        do {
            used[j0] = true;
            cur = p[j0];
            delta = INT_MAX;
            v = -1;

            for (int j = 1; j <= n; ++j) {
                if (!used[j]) {
                    if (a[cur][j] + ln[cur] + col[j] < minv[j]) {
                        minv[j] = a[cur][j] + ln[cur] + col[j];
                        way[j] = j0;
                    }
                    if (minv[j] < delta) {
                        delta = minv[j];
                        v = j;
                    }
                }
            }

            for (int j = 0; j <= n; ++j) {
                if (used[j]) {
                    ln[p[j]] -= delta;
                    col[j] += delta;
                } else {
                    minv[j] -= delta;
                }
            }

            j0 = v;
        } while (p[j0] != -1);
        do {
            int j1 = way[j0];
            p[j0] = p[j1];
            j0 = j1;
        } while (j0);

    }

    int result = 0;
    for (int i = 1; i <= n; ++i) {
        result += a[p[i]][i];
    }

    cout << result << "\n";
    for (int i = 1; i <= n; ++i) {
        cout << p[i] << " " << i << endl;
    }

    return 0;
}