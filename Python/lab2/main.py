import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import minimize
from matplotlib.colors import Normalize
from sympy import Symbol, lambdify, parsing


# Main.Task №1 (using numpy)
def newton_method(f, x, grad, hess, eps=1e-7,
                  method='standard', callback=None):
    methods = {
        'wolfe': wolfe,
        'dichotomy': dichotomy,
        'standard': lambda *args: 1,
    }

    it = 0

    while True:
        it += 1

        if callback:
            callback(x)

        gradient = grad(x)
        hessian_inv = np.linalg.inv(hess(x))

        delta = hessian_inv.dot(np.transpose(gradient))

        x_prev = x
        x = x - methods[method](f, x, delta, grad) * delta

        if (np.linalg.norm(delta) < eps or
                np.linalg.norm(x - x_prev) < eps):
            return {'x': x, 'fun': f(x), 'it': it}


# Main.Task №2
def dichotomy(f, x, grad, _, eps=1e-7):
    l = eps
    r = 100.0
    delta = eps / 2

    def foo(xi):
        return [x[i] - xi * grad[i]
                for i in range(len(x))]

    while not r - l < eps:
        x1 = (l + r - delta) / 2
        x2 = (l + r + delta) / 2
        if f(foo(x1)) < f(foo(x2)):
            r = x2
        else:
            l = x1

    return (r + l) / 2


# Main.Task №3 (using scipy.optimize)
def scipy_newton_method(f, x, jac, eps=1e-5,
                        method='Newton-CG', callback=None):
    assert method in ('Newton-CG', 'BFGS')
    return minimize(f, x, jac=jac, tol=eps,
                    method=method, callback=callback)


# Additional.Task №1
def wolfe(f, x, delta, grad, c1=1e-4, c2=0.9, it=100):
    alpha = 1.0
    alpha_min = 0.0
    alpha_max = np.inf

    delta = -delta

    f_val = f(x)
    grad_val = grad(x)

    for i in range(it):
        x_new = x + alpha * delta
        f_val_new = f(x_new)

        if f_val_new <= f_val + c1 * alpha * np.dot(grad_val, delta):
            if np.dot(grad(x_new), delta) >= c2 * np.dot(grad_val, delta):
                break
            else:
                alpha_min = alpha
                alpha = (alpha_min + alpha_max) / 2
        else:
            alpha_max = alpha
            alpha = (alpha_min + alpha_max) / 2

        alpha /= 2

    return alpha


def level_lines(x, surface_func, bounds=(-10, 10), num=100):
    fig = plt.figure()
    axes = fig.add_subplot(111)
    axes.set_xlabel('X')
    axes.set_ylabel('Y')
    axes.set_title('Level lines')

    xgrid, ygrid = np.meshgrid(np.linspace(bounds[0], bounds[1], num),
                               np.linspace(bounds[0], bounds[1], num))

    norm = Normalize(vmin=0, vmax=len(x))
    for i in range(len(x)):
        color = plt.cm.cool(norm(i))
        plt.scatter(*x[i], s=40, color=color)

    axes.contour(xgrid, ygrid, surface_func([xgrid, ygrid]))


def create_surface(x_val, fun_val, surface_func, bounds=(-10, 10), num=100):
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')

    X, Y = np.meshgrid(np.linspace(bounds[0], bounds[1], num),
                       np.linspace(bounds[0], bounds[1], num))
    Z = surface_func(np.array([X, Y]))

    ax.plot_surface(X, Y, Z, cmap='BuPu')
    colors = np.linspace(0, 1, len(x_val))

    for i in range(len(x_val)):
        ax.plot(*x_val[i], fun_val[i], '.',
                label='top', zorder=4, markersize=5, c=plt.cm.cool(colors[i]))


def main():
    """
    :MAIN:
    Rosenbrock functions:
    f = (3 - x)^2 + 20 * (y - x^2)^2
    f = (1 - x)^2 + 100 * (y - x^2)^2
    Polynomial function:
    f = (x^2 + y - 11)^2 + (x + y^2 - 7)^2
    f = (x + 2 * y - 7)^2 + (2 * x + y - 5)^2
    Non-polynomial function:
    f = sin(x + y) + (x - y)^2 - 1.5 * x + 2.5 * y + 1
    f = -cos(x) * cos(y) * exp(-((x - pi)^2 + (y - pi)^2))
    """

    x, y = Symbol('x'), Symbol('y')
    parse = '(3 - x)**2 + 20 * (y - x**2)**2'
    # parse = '(1 - x)**2 + 100 * (y - x**2)**2'
    # parse = '(x**2 + y - 11)**2 + (x + y**2 - 7)**2'
    # parse = '(x + 2 * y - 7)**2 + (2 * x + y - 5)**2'
    # parse = 'sin(x + y) + (x - y)**2 - 1.5 * x + 2.5 * y + 1'
    # parse = '-cos(x) * cos(y) * exp(-((x - pi)**2 + (y - pi)**2))'

    foo = parsing.parse_expr(parse)

    f = lambdify((x, y), foo)

    f_x = lambdify((x, y), foo.diff(x))
    f_y = lambdify((x, y), foo.diff(y))

    f_x_x = lambdify((x, y), foo.diff(x).diff(x))
    f_x_y = lambdify((x, y), foo.diff(x).diff(y))
    f_y_x = lambdify((x, y), foo.diff(y).diff(x))
    f_y_y = lambdify((x, y), foo.diff(y).diff(y))

    def fun(vec):
        return f(*vec)

    def fun_jac(vec):
        return np.array([
            f_x(*vec),
            f_y(*vec)
        ])

    def fun_hess(vec):
        return np.array([
            [f_x_x(*vec), f_x_y(*vec)],
            [f_y_x(*vec), f_y_y(*vec)]
        ])

    x_val, fun_val = [], []

    def callback(hist):
        x_val.append(hist)
        fun_val.append(fun(hist))

    result = newton_method(fun, np.array([1, 1]), fun_jac,
                           fun_hess, method='dichotomy', callback=callback)

    print(result)

    if x_val and fun_val:
        level_lines(x_val, lambda vec: fun(vec))

        create_surface(x_val, fun_val, lambda vec: fun(vec))

        plt.show()


if __name__ == '__main__':
    main()
