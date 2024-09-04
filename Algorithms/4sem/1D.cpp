#include <iostream>
#include <vector>

using namespace std;

vector<vector<int>> A;
vector<bool> used;
struct v{
    int x, y;
};

struct event {
    int time;
    v start;
    v finish;
};

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
bool dfs2(int v) {
    if (used[v]) return false;
    used[v] = true;
    if (B[v] != -1) dfs2(B[v]);
    return true;
}


int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    int m;
    cin >> m;

    int previousTimestamp = 0;
    int k = 0;

    B.resize(m, -1);
    vector<event> vec;
    used.resize(m);

    for (int i = 0; i < m; i++) {
        int hour, minute, a, b, c, d;
        char colon;
        cin >> hour >> colon >> minute >> a >> b >> c >> d;

        int timestamp = k + 60 * hour + minute;
        k += (timestamp < previousTimestamp) * 24 * 60;

        vec.push_back({k + 60 * hour + minute, {a, b}, {c, d}});
        previousTimestamp = timestamp;
    }

    A.resize(m);

    for (int i = 0; i < m; i++) {
        for (int j = i + 1; j < m; j++) {
            int tmp1 = abs(vec[i].start.x - vec[i].finish.x) + abs(vec[i].start.y - vec[i].finish.y);
            int tmp2 = abs(vec[i].finish.x - vec[j].start.x) + abs(vec[i].finish.y - vec[j].start.y);
            if (vec[i].time + tmp1 + tmp2 < vec[j].time) {
                A[i].push_back(j);
            }
        }
    }

    for (int i = 0; i < m; i++) {
        if (dfs(i)) {
            fill(used.begin(), used.end(), false);
        }
    }

    fill(used.begin(), used.end(), false);
    int p = 0;
    int cnt = 0;
    for (int i = m - 1; i >= 0; i--) {
        p += dfs2(i);
    }
    cnt += p;
    cout << cnt << endl;

}