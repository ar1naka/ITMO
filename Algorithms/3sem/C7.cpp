#pragma GCC optimize("O3")

#include <vector>
#include <iostream>

using namespace std;

vector<long long> arr;

struct DSU {
private:
    vector<int> parent;

public:
    void dsu_fill(int n) {
        for (int i = 0; i <= n; i++) {
            parent.push_back(i);
        }
    }

    int dsu_find(int x) {
        if (x == parent[x]) {
            return x;
        }
        parent[x] = dsu_find(parent[x]);
        return parent[x];
    }

    void dsu_union(int x, int y) {
        x = dsu_find(x);
        y = dsu_find(y);
        parent[x] = y;
        arr[y] += arr[x];
    }
};

int main() {
    int n;
    cin >> n;

    arr.resize(n + 1, 0);
    for (size_t i = 1; i <= n; i++) {
        cin >> arr[i];
    }

    vector<int> perm;
    perm.resize(n + 1, 0);
    for (size_t i = 1; i <= n; i++) {
        cin >> perm[i];
    }

    vector<int> used;
    used.resize(n + 2, 0);
    vector<long long> answer;
    answer.resize(n + 1, 0);

    DSU dsu;
    dsu.dsu_fill(n);
    for (size_t i = n; i > 0; i--) {
        used[perm[i]] = 1;
        if (used[perm[i] - 1]) {
            dsu.dsu_union(perm[i], perm[i] - 1);
        }
        if (used[perm[i] + 1]) {
            dsu.dsu_union(perm[i], perm[i] + 1);
        }
        answer[i - 1] = max(answer[i], arr[dsu.dsu_find(perm[i])]);
    }

    for (size_t i = 1; i <= n; i++) {
        cout << answer[i] << endl;
    }

    return 0;
}