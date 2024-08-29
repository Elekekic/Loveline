import { Component } from '@angular/core';
import { gsap } from 'gsap';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.scss'],
})
export class LandingPageComponent {
  private progressColors = ['#5a1d2d', '#cf617a', '#5694a7', '#457192'];

  constructor() {}

  ngOnInit(): void {
    this.animateLoader();
    this.darkOrLightMode();
    this.iconsSideMenuAnimations();
  }

  iconsSideMenuAnimations(): void {
    document.querySelectorAll('.side-menu a').forEach(function (link) {
      const svg = link.querySelector('svg');

      link.addEventListener('mouseenter', function () {
        gsap.to(svg, {
          duration: 0.1,
          opacity: 1,
          scale: 1.1,
          ease: 'power4.out',
        });
      });

      link.addEventListener('mouseleave', function () {
        gsap.to(svg, {
          duration: 0.1,
          opacity: 0,
          scale: 0.5,
          ease: 'power4.in',
        });
      });
    });
  }

  animateLoader(): void {
    const loader = gsap.timeline({ onComplete: this.showContent });

    loader
      .from('.img-left', {
        duration: 1.5,
        opacity: 0,
        x: '-150%',
        ease: 'power4.out',
      })
      .from(
        '.img-right',
        { duration: 1.5, opacity: 0, x: '150%', ease: 'power4.out' },
        '-=1.5'
      )
      .to('.img-left', { duration: 0.5, rotation: -18, x: '-30%' })
      .to('.img-right', { duration: 0.5, rotation: 18, x: '30%' }, '-=0.5')
      .to('.container-text', { duration: 0.2, opacity: 1 })
      .to('.loading-bar', { duration: 0.2, opacity: 1 })
      .to('.counter', {
        innerHTML: 100,
        duration: 1,
        ease: 'none',
        snap: { innerHTML: 1 },
        onUpdate: () => {
          const counter = document.querySelector('.counter');
          const progressBar: HTMLElement | null =
            document.querySelector('.progress-bar');

          if (counter && progressBar) {
            const progressValue = Math.round(parseInt(counter.innerHTML));
            counter.innerHTML = progressValue.toString();
            progressBar.style.width = `${progressValue}%`;

            this.updateProgressBarColor(progressValue);
          }
        },
      });
  }

  updateProgressBarColor(progressValue: number): void {
    const colorIndex = Math.floor(
      progressValue / (100 / this.progressColors.length)
    );
    const color = this.progressColors[colorIndex];

    gsap.to('.progress-bar', {
      backgroundColor: color,
      duration: 0.2,
      ease: 'none',
    });

    gsap.to('.counter', {
      color: color,
      duration: 0.2,
      ease: 'none',
    });
  }

  showContent(): void {
    gsap.to('.loader', { duration: 0.4, opacity: 0, display: 'none' });
    gsap.to('body, html', { overflow: 'auto' });
  }

  darkOrLightMode(): void {
    const themeToggleButton = document.getElementById(
      'theme-button'
    ) as HTMLElement | null;
    const iconMoon = document.getElementById('icon-moon') as SVGElement | null;
    const iconSun = document.getElementById('icon-sun') as SVGElement | null;

    if (themeToggleButton && iconMoon && iconSun) {
      themeToggleButton.addEventListener('click', () => {
        const body = document.body;

        body.classList.toggle('dark-mode');

        if (body.classList.contains('dark-mode')) {
          iconMoon.style.display = 'none';
          iconSun.style.display = 'block';
        } else {
          iconMoon.style.display = 'block';
          iconSun.style.display = 'none';
        }
      });
    }
  }
}
